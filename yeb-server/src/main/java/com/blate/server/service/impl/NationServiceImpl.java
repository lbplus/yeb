package com.blate.server.service.impl;

import com.blate.server.pojo.Nation;
import com.blate.server.mapper.NationMapper;
import com.blate.server.service.INationService;
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
public class NationServiceImpl extends ServiceImpl<NationMapper, Nation> implements INationService {

}
