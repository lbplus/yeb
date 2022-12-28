package com.blate.server.service.impl;

import com.blate.server.pojo.MailLog;
import com.blate.server.mapper.MailLogMapper;
import com.blate.server.service.IMailLogService;
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
public class MailLogServiceImpl extends ServiceImpl<MailLogMapper, MailLog> implements IMailLogService {

}
