package com.blate.server.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.blate.server.pojo.Employee;
import com.blate.server.pojo.MailConstants;
import com.blate.server.pojo.MailLog;
import com.blate.server.service.IEmployeeService;
import com.blate.server.service.IMailLogService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件发送定时任务
 *
 * @author blate
 * @since 1.0.0
 */
@Component
public class MailTask {

    @Autowired
    private IMailLogService mailLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IEmployeeService employeeService;

    /**
     * 邮件发送定时任务
     * 10秒一次
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void mailTask() {
        //状态为0且重试时间小于当前时间的才需要重新发送(假如1.01发送消息，trytime设置为1分钟，那就是1.01后需要重试，所以检测只要过了1.01点并且状态为0就可重试了)
        List<MailLog> list = mailLogService.list(new QueryWrapper<MailLog>().eq("status", 0).lt("tryTime", LocalDateTime.now()));
        list.forEach(mailLog -> {
            //重试次数超过3次，更新为投递失败，不再重试
            if (mailLog.getCount() >= MailConstants.MAX_TRY_COUNT) {
                mailLogService.update(new UpdateWrapper<MailLog>().set("status", 2).eq("msgId", mailLog.getMsgId()));
            }
            //更新重试次数，更新时间，重试时间
            mailLogService.update(new UpdateWrapper<MailLog>()
                    .set("count", mailLog.getCount() + 1)
                    .set("updateTime", LocalDateTime.now())
                    .set("tryTime", LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT))//继续1分钟后重试
                    .eq("msgId", mailLog.getMsgId()));
            Employee employee = employeeService.getEmployee(mailLog.getEid()).get(0);
            System.out.println("MailTask: employee = " + employee);
            System.out.println("MailTask: msgId: " + mailLog.getMsgId());
            //发送消息
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, employee, new CorrelationData(mailLog.getMsgId()));
        });
    }

}
