package com.blate.server.service.impl;

import com.blate.server.pojo.Role;
import com.blate.server.mapper.RoleMapper;
import com.blate.server.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
