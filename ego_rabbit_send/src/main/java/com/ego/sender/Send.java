package com.ego.sender;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 保证当前项目被其他项目依赖，其他项目的启动器能扫描到这个类
 */
@Component
public class Send {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String queue,Object object){
        amqpTemplate.convertAndSend("amq.direct",queue,object);
    }

    /**
     * 此方法返回值就是receive方法的返回值
     * @param queue
     * @param object
     * @return
     */
    public Object sendAndReceive(String queue,Object object){
        return amqpTemplate.convertSendAndReceive("amq.direct",queue,object);
    }

}
