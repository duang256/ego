package com.ego.sender.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有队列，帮助创建队列
 */
@Configuration
public class SenderConfig {
    @Value("${ego.rabbitmq.content.queuename}")
    private String queuename;

    @Bean
    public Queue queue(){
        return new Queue(queuename);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("amq.direct");
    }

    //参数名和方法名一致就是从spring容器中回去方法返回值
    //新建队列与amp.direct交换器绑定
    @Bean
    public Binding binding(Queue queue,DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).withQueueName();
    }
}
