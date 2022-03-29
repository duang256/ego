package com.ego.receive;

import com.ego.commons.utils.HttpClientUtil;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class QueueListener {
    //方案1是通过变为consumer，引入redis和provider，然后通过RedisTemplate更新redis缓存，不过还需要配置RedisTemplate配置类
    //    @Reference
    //    private TbContentDubboService tbContentDubboService;
    //    @Value("${ego.bigad.categoryId}")
    //    private long bigadId;
    //    @Autowired
    //    private RedisTemplate redisTemplate;

    @Value("${ego.portal.location}")
    private String portalLocation;
    @Value("${ego.search.location}")
    private String searchLocation;

    //即使没有发送队列，启动receiver也会创建
    @RabbitListener(bindings = {@QueueBinding(value=@Queue(name="${ego.rabbitmq.content.queuename}"),exchange = @Exchange(name = "amq.direct"))})
    public void content(Object object){
        /*
            方案2.即调用portal中的接口进行代码复用缓存到redis
         */
        System.out.println("接收到消息");
        HttpClientUtil.doGet(portalLocation + "bigadUpdate");
    }


    @RabbitListener(bindings = {@QueueBinding(value=@Queue(name="${ego.rabbitmq.item.insertName}"),exchange = @Exchange(name = "amq.direct"))})
    public void insertItem(String id){
        System.out.println("接收到新增id" + id);

        Map<String,String> map = new HashMap<>();
        map.put("ids",id);
        HttpClientUtil.doGet(searchLocation + "insert",map);
    }

    @RabbitListener(bindings = {@QueueBinding(value=@Queue(name="${ego.rabbitmq.item.deleteName}"),exchange = @Exchange(name = "amq.direct"))})
    public void deleteItem(String id){
        System.out.println("接收到删除id" + id);

        Map<String,String> map = new HashMap<>();
        map.put("ids",id);
        HttpClientUtil.doGet(searchLocation + "delete",map);
    }

}
