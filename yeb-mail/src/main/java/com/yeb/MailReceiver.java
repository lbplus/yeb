package com.yeb;

import com.blate.server.pojo.Employee;
import com.blate.server.pojo.MailConstants;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

/**
 * 消息接收者
 *
 * @author blate
 */
@Component
public class MailReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MailReceiver.class);

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 邮件发送
     */
    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void handler(Message message, Channel channel) {

        Employee employee = (Employee) message.getPayload();
        System.out.println("MailReceiver:  employee = " + employee);
        MessageHeaders headers = message.getHeaders();
        long tag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
//        long tag = (long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        System.out.println("tag = " + tag);
        String msgId = (String) headers.get("spring_returned_message_correlation");
        System.out.println("msgId = " + msgId);
        HashOperations hash = redisTemplate.opsForHash();

        try {
            if (hash.entries("mail_log").containsKey(msgId)) {
                //redis中包含key，说明消息已经被消费
                logger.info("消息已经被消费========>{}", msgId);
                /**
                 * 消费成功，执行basic.ack
                 * 手动确认消息
                 * tag:消息id
                 * multiple:如果为 false，表示仅确认当前消息消费成功，如果为 true，则表示当前消息之前所有未被当前消费者确认的消息都消费成功
                 */
                channel.basicAck(tag, false);
                return;
            }
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg);
            helper.setFrom(mailProperties.getUsername());//发件人
            helper.setTo(employee.getEmail());//收件人
            helper.setSubject("入职邮件");//主题
            helper.setSentDate(new Date());//发送日期
            Context context = new Context();//邮件内容
            context.setVariable("name", employee.getName());//main.html的名字
            context.setVariable("posName", employee.getPosition().getName());//main.html的职位
            context.setVariable("joblevelName", employee.getJoblevel().getName());//main.html的职称
            context.setVariable("departmentName", employee.getDepartment().getName());//main.html的部门
            String mail = templateEngine.process("mail", context);//将上面的内容引用到mail变量里面
            helper.setText(mail, true);//发送正文
            javaMailSender.send(msg);//发送邮件
            logger.info("邮件发送成功");
            //将消息id存入redis
            hash.put("mail_log", msgId, "OK");
            System.out.println("MailReceiver: redis---> msgId = " + msgId);
            //手动确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                /**
                 * 消费失败，执行basic.nck
                 * 手动确认消息
                 * tag：消息id
                 * b:如果为 false，表示仅拒绝当前消息的消费，如果为 true，则表示拒绝当前消息之前所有未被当前消费者确认的消息
                 * b1:是否退回到队列
                 */
                channel.basicNack(tag, false, true);
            } catch (IOException ioException) {
                //ioException.printStackTrace();
                logger.error("消息确认失败=====>{}", ioException.getMessage());
            }
            logger.error("MailReceiver + 邮件发送失败========{}", e.getMessage());
        }
    }

    /**
     * 邮件发送测试
     */
    @RabbitListener(queues = "mail.demo")
    public void handler2(String s) {

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        try {
            //发件人
            helper.setFrom(mailProperties.getUsername());
            //收件人
            helper.setTo("2250800625@qq.com");
            //主题
            helper.setSubject("测试邮件");
            //发送日期
            helper.setSentDate(new Date());
            //邮件内容
//            Context context = new Context();
            helper.setText("你好测试邮件已送到");
            //发送邮件
            javaMailSender.send(msg);
            logger.info("邮件发送成功");
        } catch (MessagingException e) {
            logger.error("MailReceiver + 邮件发送失败========{}", e.getMessage());
        }
    }


}