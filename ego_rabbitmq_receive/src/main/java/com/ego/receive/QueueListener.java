package com.ego.receive;

import com.ego.commons.pojo.BigAd;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbContent;
import com.ego.pojo.TbItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class QueueListener {
    //redis同步缓存
    //solr异步缓存

    @Reference
    private TbItemDubboService tbItemDubboService;

    @Reference
    private TbContentDubboService tbContentDubboService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${ego.search.location}")
    private String searchLocation;

    @Value("${ego.bigad.categoryId}")
    private Long categoryId;

    //即使没有发送队列，启动receiver也会创建
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.content.queuename}"), exchange = @Exchange(name = "amq.direct"))})
    public void content(Object object) {
        System.out.println("接收到更新广告redis消息");

        String key = "com.ego.portal::bigad";
        List<TbContent> list = tbContentDubboService.selectAllByCategoryIdOrder(categoryId);
        List<BigAd> adList = new ArrayList<>();

        for(TbContent tbContent: list){
            BigAd bigAd = new BigAd();
            bigAd.setSrc(tbContent.getPic());
            bigAd.setAlt("");
            bigAd.setHeight(240);
            bigAd.setHeightB(240);
            bigAd.setSrcB(tbContent.getPic2());
            bigAd.setWidth(670);
            bigAd.setWidthB(550);
            bigAd.setHref(tbContent.getUrl());
            adList.add(bigAd);
        }
        //list数据转换为JSON字符串，Jackson转换
        redisTemplate.opsForValue().set(key,JsonUtils.objectToJson(adList));
    }


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.item.insertName}"), exchange = @Exchange(name = "amq.direct"))})
    public void insertItem(String id) {
        System.out.println("接收到新增id" + id);

        Map<String, String> map = new HashMap<>();
        map.put("ids", id);
        HttpClientUtil.doGet(searchLocation + "insert", map);

        String[] ids = id.split(",");
        for(String idArr : ids){
            String key = "com.ego.item::showItem:" + idArr;
            TbItem tbItem = tbItemDubboService.selectById(Long.parseLong(idArr));
            TbItemDetails details = new TbItemDetails();
            details.setId(Long.parseLong(idArr));
            details.setPrice(tbItem.getPrice());
            details.setSellPoint(tbItem.getSellPoint());
            details.setTitle(tbItem.getTitle());
            details.setImages(tbItem.getImage() != null && !tbItem.getImage().equals("") ? tbItem.getImage().split(",") : new String[1]);
            redisTemplate.opsForValue().set(key,details);
        }
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.item.deleteName}"), exchange = @Exchange(name = "amq.direct"))})
    public void deleteItem(String id) {
        System.out.println("接收到删除id" + id);

        Map<String, String> map = new HashMap<>();
        map.put("ids", id);
        HttpClientUtil.doGet(searchLocation + "delete", map);


        String[] ids = id.split(",");
        for(String idArr : ids){
            String key = "com.ego.item::showItem:" + idArr;
            redisTemplate.delete(key);
        }
    }

}
