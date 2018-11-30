package com.noriental;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

/**
 * Created by chendengyu on 2016/2/18.
 */
public class ReSendClip {

    public static void main(String[] args) throws IOException {
        System.out.println("开始消息");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("172.18.4.48");
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("10.60.0.63");
        connectionFactory.setPort(5555);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("dsjw2014");
        RabbitTemplate rabbit = new RabbitTemplate(connectionFactory);
        rabbit.afterPropertiesSet();

        rabbit.convertAndSend("convertToPngQueue", "rw_CRTzDDpP0I.pptx");

        System.out.println("结束消息");
        System.exit(0);
    }

}
