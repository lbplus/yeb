package com.blate.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blate.server.config.security.component.JwtTokenUtils;
import com.blate.server.mapper.AdminRoleMapper;
import com.blate.server.mapper.RoleMapper;
import com.blate.server.pojo.Admin;
import com.blate.server.mapper.AdminMapper;
import com.blate.server.pojo.AdminRole;
import com.blate.server.pojo.RespBean;
import com.blate.server.pojo.Role;
import com.blate.server.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blate.server.service.IMenuService;
import com.blate.server.utils.AdminUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private UserDetailsService userDetailsService;

    //加密方式（在SecurityConfig选择生成哪种）
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private AdminRoleMapper adminRoleMapper;

    @Autowired
    private IMenuService menuService;

    /**
     * 登录之后返回token
     *
     * @param username
     * @param password
     * @param code
     * @param request
     * @return
     */
    @Override
    public RespBean login(String username, String password, String code, HttpServletRequest request) {

        //获取系统验证码
        String captcha = (String) request.getSession().getAttribute("captcha");
        System.out.println("username：" + username);
        System.out.println("password：" + password);
        System.out.println("用户验证码：" + code);
        System.out.println("系统验证码：" + captcha);
        if (StringUtils.isEmpty(code) || !captcha.equalsIgnoreCase(code)) {
            return RespBean.error("验证码不正确，请重新输入！");
        }

        //登录
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (null == userDetails || !passwordEncoder.matches(password, userDetails.getPassword())) {
            return RespBean.error("用户名密码错误");
        }

        //判断账号是否被禁用
        if (!userDetails.isEnabled()) {
            return RespBean.error("账号被禁用,请联系管理员!");
        }

        //更新security登陆用户对象，这样对象就在SpringSecurity全文中更新
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //生成token
        String token = jwtTokenUtils.generateToken(userDetails);
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);

        return RespBean.success("登陆成功", tokenMap);

    }

    /**
     * 根据用户名获取用户
     *
     * @param username
     * @return
     */
    @Override
    public Admin getAdminByUserName(String username) {
        return adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", username).eq("enabled", true));
    }

    /**
     * 根据用户id查询角色列表
     *
     * @param adminId
     * @return
     */
    @Override
    public List<Role> getRoles(Integer adminId) {
        return roleMapper.getRoles(adminId);
    }

    /**
     * 获取所有操作员
     *
     * @param keywords
     * @return
     */
    @Override
    public List<Admin> getAllAdmins(String keywords) {
        return adminMapper.getAllAdmins(AdminUtils.getCurrentAdmin().getId(), keywords);
    }

    /**
     * 更新操作员角色
     *
     * @param adminId
     * @param rids
     * @return
     */
    @Override
    @Transactional
    public RespBean updateAdminRole(Integer adminId, List<Integer> rids) {
        adminRoleMapper.delete(new QueryWrapper<AdminRole>().eq("adminId", adminId));
        Integer result = adminRoleMapper.addAdminRole(adminId, rids);
        if (result == rids.size()) {
            menuService.updateRedisMenu();//更新用户redis的菜单
            return RespBean.success("更新成功！");
        }
        return RespBean.error("更新失败！");
    }

    /**
     * 个人中心 修改用户密码
     *
     * @param oldPass
     * @param pass
     * @param adminId
     * @return
     */
    @Override
    public RespBean updateAdminPassWord(String oldPass, String pass, Integer adminId) {
        Admin admin = adminMapper.selectById(adminId);
        // 密码是加密过的
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 比较旧密码是否和未更新的数据库密码是否一样，不一样则不让修改
        if (encoder.matches(oldPass, admin.getPassword())) {
            // 设置新密码，加密
            admin.setPassword(encoder.encode(pass));
            int result = adminMapper.updateById(admin);
            System.out.println(result);
            if (result == 1) {
                return RespBean.success("更新成功!");
            }
        }
        return RespBean.error("更新失败！");
    }

    /**
     * 更新用户头像
     *
     * @param url
     * @param id
     * @param authentication
     * @return
     */
    @Override
    public RespBean updateAdminUserFace(String url, Integer id, Authentication authentication) {
        Admin admin = adminMapper.selectById(id);
        admin.setUserFace(url);
        int result = adminMapper.updateById(admin);
        if (result == 1) {
            //更新当前用户的全局信息
            Admin principal = (Admin) authentication.getPrincipal();
            principal.setUserFace(url);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(admin, null, authentication.getAuthorities()));
            return RespBean.success("更新成功！", url);
        }
        return RespBean.error("更新失败！");
    }

}
