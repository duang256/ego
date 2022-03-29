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

    @Value("${ego.rabbitmq.item.insertName}")
    private String insertName;

    @Value("${ego.rabbitmq.item.deleteName}")
    private String deleteName;

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



    /*
    solr异步同步消息队列 新增商品
    同样的，如果启动时没有该队列，则创建${ego.rabbitmq.item.insertName}队列
     */
    @Bean
    public Queue queueInsertItem(){
        return new Queue(insertName);
    }


    //参数名和方法名一致就是从spring容器中回去方法返回值
    //新建队列与amp.direct交换器绑定
    @Bean
    public Binding bindingInsertItem(Queue queueInsertItem,DirectExchange directExchange){
        return BindingBuilder.bind(queueInsertItem).to(directExchange).withQueueName();
    }


    /**
     * solr异步同步消息队列 删除商品
     * 同样的，如果启动时没有该队列，则创建${ego.rabbitmq.item.deleteName}队列
     */
    @Bean
    public Queue queueDeleteItem(){
        return new Queue(deleteName);
    }

    @Bean
    public Binding bindingDeleteItem(Queue queueDeleteItem,DirectExchange directExchange){
        return BindingBuilder.bind(queueDeleteItem).to(directExchange).withQueueName();
    }

}
