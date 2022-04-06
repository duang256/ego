package com.ego.receive;

import com.ego.commons.pojo.BigAd;
import com.ego.commons.pojo.DeleteCartPojo;
import com.ego.commons.pojo.OrderPojo;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.commons.utils.IDUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.dubbo.service.TbOrderDubboService;
import com.ego.mail.EgoMailSender;
import com.ego.pojo.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;


@Component
public class QueueListener {
    //redis同步缓存
    //solr异步缓存

    @Reference
    private TbItemDubboService tbItemDubboService;

    @Reference
    private TbContentDubboService tbContentDubboService;

    @Reference
    private TbOrderDubboService tbOrderDubboService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${ego.search.location}")
    private String searchLocation;

    @Value("${ego.bigad.categoryId}")
    private Long categoryId;

    @Value("${ego.cart.location}")
    private String cartLocation;

    @Autowired
    private EgoMailSender egoMailSender;


    //即使没有发送队列，启动receiver也会创建
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.content.queuename}"), exchange = @Exchange(name = "amq.direct"))})
    public void content(Object object) {
        System.out.println("接收到更新广告redis消息");

        String key = "com.ego.portal::bigad";
        List<TbContent> list = tbContentDubboService.selectAllByCategoryIdOrder(categoryId);
        List<BigAd> adList = new ArrayList<>();

        for (TbContent tbContent : list) {
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
        redisTemplate.opsForValue().set(key, JsonUtils.objectToJson(adList));
    }


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.item.insertName}"), exchange = @Exchange(name = "amq.direct"))})
    public void insertItem(String id) {
        System.out.println("接收到新增id" + id);

        Map<String, String> map = new HashMap<>();
        map.put("ids", id);
        HttpClientUtil.doGet(searchLocation + "insert", map);

        String[] ids = id.split(",");
        for (String idArr : ids) {
            String key = "com.ego.item::showItem:" + idArr;
            TbItem tbItem = tbItemDubboService.selectById(Long.parseLong(idArr));
            TbItemDetails details = new TbItemDetails();
            details.setId(Long.parseLong(idArr));
            details.setPrice(tbItem.getPrice());
            details.setSellPoint(tbItem.getSellPoint());
            details.setTitle(tbItem.getTitle());
            details.setImages(tbItem.getImage() != null && !tbItem.getImage().equals("") ? tbItem.getImage().split(",") : new String[1]);
            redisTemplate.opsForValue().set(key, details);
        }
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.item.deleteName}"), exchange = @Exchange(name = "amq.direct"))})
    public void deleteItem(String id) {
        System.out.println("接收到删除id" + id);

        Map<String, String> map = new HashMap<>();
        map.put("ids", id);
        HttpClientUtil.doGet(searchLocation + "delete", map);


        String[] ids = id.split(",");
        for (String idArr : ids) {
            String key = "com.ego.item::showItem:" + idArr;
            redisTemplate.delete(key);
        }
    }


    /**
     * 创建订单
     * 当接收消息带有返回值时，发送方必须使用可接受状态的方法
     *
     * @param message
     */
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.order.createOrder}"), exchange = @Exchange(name = "amq.direct"))})
    public String createOrder(Message message) {
        //序列化和反序列化
        try {
            byte[] body = message.getBody();
            InputStream is = new ByteArrayInputStream(body);
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            OrderPojo orderPojo = (OrderPojo) objectInputStream.readObject();
            //获取所有商品List
            List<TbOrderItem> orderItems = orderPojo.getOrderItems();
            //判断库存
            for (TbOrderItem item : orderItems) {
                TbItem tbItem = tbItemDubboService.selectById(Long.parseLong(item.getItemId()));
                if (tbItem.getNum() < item.getNum()) {
                    //库存不足
                    return null;
                }
            }

            //执行至此说明库存足够开始新增
            TbOrder tbOrder = new TbOrder();
            String id = IDUtils.genItemId() + "";
            Date date = new Date();
            tbOrder.setPayment(orderPojo.getPayment());
            tbOrder.setPaymentType(orderPojo.getPaymentType());
            tbOrder.setOrderId(id);
            tbOrder.setCreateTime(date);
            tbOrder.setUpdateTime(date);

            List<TbOrderItem> itemList = orderPojo.getOrderItems();
            for (TbOrderItem item : itemList) {
                item.setId(IDUtils.genItemId() + "");
                item.setOrderId(id);
            }
            TbOrderShipping orderShipping = orderPojo.getOrderShipping();
            orderShipping.setOrderId(id);
            orderShipping.setCreated(date);
            orderShipping.setUpdated(date);

            int index = tbOrderDubboService.insert(tbOrder, itemList, orderShipping);
            if (index == 1) {
                return id;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.order.deleteCart}"), exchange = @Exchange(name = "amq.direct"))})
    public void deleteCart(Message message) {
        //序列化和反序列化
        try {
            byte[] body = message.getBody();
            InputStream is = new ByteArrayInputStream(body);
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            DeleteCartPojo deleteCartPojo = (DeleteCartPojo) objectInputStream.readObject();

            Map<String,String> param = new HashMap<>();
            param.put("userId",deleteCartPojo.getUserId() + "");
            param.put("ids",deleteCartPojo.getItemIds());

            HttpClientUtil.doPost(cartLocation + "cart/deleteByIds",param);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.mail}"), exchange = @Exchange(name = "amq.direct"))})
    public void mail(String orderId) {
        egoMailSender.send("2608194130@qq.com",orderId);
    }

}
