package com.blate.server.service;

import com.blate.server.pojo.MenuRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blate.server.pojo.RespBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
public interface IMenuRoleService extends IService<MenuRole> {

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    RespBean updateMenuRole(Integer rid, List<Integer> mids);
}
