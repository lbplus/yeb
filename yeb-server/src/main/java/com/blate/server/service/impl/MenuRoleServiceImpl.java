package com.blate.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blate.server.pojo.MenuRole;
import com.blate.server.mapper.MenuRoleMapper;
import com.blate.server.pojo.RespBean;
import com.blate.server.service.IMenuRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blate.server.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
@Service
public class MenuRoleServiceImpl extends ServiceImpl<MenuRoleMapper, MenuRole> implements IMenuRoleService {

    @Resource
    private MenuRoleMapper menuRoleMapper;

    @Autowired
    private IMenuService menuService;

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    @Override
    @Transactional//这里有事务！
    public RespBean updateMenuRole(Integer rid, List<Integer> mids) {
        menuRoleMapper.delete(new QueryWrapper<MenuRole>().eq("rid",rid));
        if(null==mids||0==mids.size()){
            menuService.updateRedisMenu();//更新用户redis的菜单
            return RespBean.success("更新成功");
        }
        Integer result = menuRoleMapper.insertRecord(rid, mids);
        if(result==mids.size()){
            menuService.updateRedisMenu();//更新用户redis的菜单
            return RespBean.success("更新成功！");
        }
        return RespBean.error("更新失败！");
    }

}
