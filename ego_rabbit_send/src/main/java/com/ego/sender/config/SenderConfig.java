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

    @Value("${ego.rabbitmq.order.createOrder}")
    private String createOrder;

    @Value("${ego.rabbitmq.order.deleteCart}")
    private String deleteCart;

    @Value("${ego.rabbitmq.mail}")
    private String mail;


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
    solr异步消息队列 新增商品
    同样的，如果启动时没有该队列，则创建${ego.rabbitmq.item.insertName}队列
     */
    @Bean
    public Queue queueInsertItem(){
        return new Queue(insertName);
    }

    @Bean
    public Binding bindingInsertItem(Queue queueInsertItem,DirectExchange directExchange){
        return BindingBuilder.bind(queueInsertItem).to(directExchange).withQueueName();
    }


    /**
     * solr异步 删除商品
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


    /**
     * 下订单，同步的，队列处理高并发
     * @return
     */
    @Bean
    public Queue queuecreateOrder(){
        return new Queue(createOrder);
    }

    @Bean
    public Binding bindingcreateOrder(Queue queuecreateOrder,DirectExchange directExchange){
        return BindingBuilder.bind(queuecreateOrder).to(directExchange).withQueueName();
    }



    /**
     *
     * 下订单成功后异步删除购物车
     */
    @Bean
    public Queue queueDeleteCart(){
        return new Queue(deleteCart);
    }

    @Bean
    public Binding bindingDeleteCart(Queue queueDeleteCart,DirectExchange directExchange){
        return BindingBuilder.bind(queueDeleteCart).to(directExchange).withQueueName();
    }


    /**
     * 下订单成功后异步发邮件
     * @return
     */
    @Bean
    public Queue queueEmail(){
        return new Queue(mail);
    }

    @Bean
    public Binding bindingQueueEmail(Queue queueEmail,DirectExchange directExchange){
        return BindingBuilder.bind(queueEmail).to(directExchange).withQueueName();
    }

}
