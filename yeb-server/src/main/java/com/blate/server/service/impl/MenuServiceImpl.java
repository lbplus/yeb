package com.blate.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blate.server.mapper.MenuMapper;
import com.blate.server.pojo.Admin;
import com.blate.server.pojo.Menu;
import com.blate.server.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Resource
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据用户id查询菜单列表
     *
     * @return
     */
    @Override
    public List<Menu> getMenuByAdminId() {
        //用 security 自带的方法，来获取当前登录用户的基本信息，其中就包括ID
        Integer adminId = ((Admin) SecurityContextHolder
                .getContext()//获取登录用户全局上下文
                .getAuthentication()//获取验证后的信息
                .getPrincipal())//权限验证
                .getId();

        //获取redis操作api-valueOperations
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //从redis获取菜单数据
        List<Menu> menus = (List<Menu>) valueOperations.get("menu_" + adminId);
        System.out.println("menus：" + menus);
        //如果是空的，就用mapper去获取当前登录用户的菜单，当前用户id查询的
        if (CollectionUtils.isEmpty(menus)) {
            System.out.println("查询菜单走数据库了");
            menus = menuMapper.getMenusByAdminId(adminId);
            //将数据设置到redis中
            valueOperations.set("menu_" + adminId, menus);
        }
        return menus;
    }

    /**
     * 更新所有用户的redis菜单（这里比较粗鲁全部删除了）
     *
     * @return
     */
    @Override
    public void updateRedisMenu() {
        String keys = "menu_*";
        redisTemplate.delete(redisTemplate.keys(keys));
    }

    /**
     * 根据角色获取菜单列表
     *
     * @return
     */
    @Override
    public List<Menu> getMenuWithRole() {
        return menuMapper.getMenusWithRole();
    }

    /**
     * 查询所有菜单
     *
     * @return
     */
    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }

}
