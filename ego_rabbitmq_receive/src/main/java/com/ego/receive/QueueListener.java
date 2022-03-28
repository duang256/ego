package com.ego.receive;

import com.ego.commons.pojo.BigAd;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.pojo.TbContent;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueueListener {
    @Reference
    private TbContentDubboService tbContentDubboService;

    @Value("${ego.bigad.categoryId}")
    private long bigadId;
    @Autowired
    private RedisTemplate redisTemplate;
    //即使没有发送队列，启动receiver也会创建
    @RabbitListener(bindings = {@QueueBinding(value=@Queue(name="content"),exchange = @Exchange(name = "amq.direct"))})
    public void content(Object object){
        System.out.println("接收到消息");
        /*
            redis数据同步
            从mysql中取出广告数据，即将当前项目做成consumer
            然后更新缓存,即调用portal中的接口进行代码复用缓存到redis
         */
        HttpClientUtil.doGet("http://localhost:8082/bigadUpdate");
    }
}
