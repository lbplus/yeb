package com.blate.server.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.blate.server.pojo.MailConstants;
import com.blate.server.pojo.MailLog;
import com.blate.server.service.IMailLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */

@Configuration
public class RabbitMQConfig {
//public class RabbitMQConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{//也可以这样确认回调

    public static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Autowired
    private IMailLogService mailLogService;

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        /**
         * 配置消息到达交换机的确认回调，确认消息是否到达broker，这里是函数式编程
         * data:消息唯一标识
         * ack：确认结果
         * cause：失败原因
         */
        rabbitTemplate.setConfirmCallback((data,ack,cause)->{
            String msgId = data.getId();
            System.out.println("RabbitMQConfig: msgId = " + msgId);
            if (ack){
                logger.info("{}==========>消息到达交换机成功",msgId);
                mailLogService.update(new UpdateWrapper<MailLog>().set("status",1).eq("msgId",msgId));
            }else {
                logger.info("{}==========>消息到达交换机失败",msgId);
            }
        });
        /**
         * 消息路由到队列失败的回调
         * msg:消息主题
         * repCode:响应码
         * repText:响应描述
         * exchange:交换机
         * routingKey:路由键
         * */
        rabbitTemplate.setReturnCallback((msg,repCode,repText,exchange,routingKey)->{
            logger.info("{}=======================>消息发送到queue时失败",msg.getBody());
        });
        return rabbitTemplate;
    }

    @Bean
    public Queue queue() {
        return new Queue(MailConstants.MAIL_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MailConstants.MAIL_EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with(MailConstants.MAIL_ROUTING_KEY_NAME);
    }

}


//@Configuration
//public class RabbitConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
//    public static final String JAVABOY_EXCHANGE_NAME = "javaboy_exchange_name";
//    public static final String JAVABOY_QUEUE_NAME = "javaboy_queue_name";
//    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//    @Bean
//    Queue queue() {
//        return new Queue(JAVABOY_QUEUE_NAME);
//    }
//    @Bean
//    DirectExchange directExchange() {
//        return new DirectExchange(JAVABOY_EXCHANGE_NAME);
//    }
//    @Bean
//    Binding binding() {
//        return BindingBuilder.bind(queue())
//                .to(directExchange())
//                .with(JAVABOY_QUEUE_NAME);
//    }
//
//    @PostConstruct
//    public void initRabbitTemplate() {
//        rabbitTemplate.setConfirmCallback(this);
//        rabbitTemplate.setReturnsCallback(this);
//    }
//定义 initRabbitTemplate 方法并添加 @PostConstruct 注解，在该方法中为 rabbitTemplate 分别配置这两个 Callback。

//
//    @Override
//    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//        if (ack) {
//            logger.info("{}:消息成功到达交换器",correlationData.getId());
//        }else{
//            logger.error("{}:消息发送失败", correlationData.getId());
//        }
//    }
//
//    @Override
//    public void returnedMessage(ReturnedMessage returned) {
//        logger.error("{}:消息未成功路由到队列",returned.getMessage().getMessageProperties().getMessageId());
//    }
//}
