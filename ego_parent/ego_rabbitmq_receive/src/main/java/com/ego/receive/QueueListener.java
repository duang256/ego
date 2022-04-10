package com.ego.receive;

import com.ego.commons.pojo.DeleteCartPojo;
import com.ego.commons.pojo.OrderPojo;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.commons.utils.IDUtils;
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

/**
 * 读取缓存步骤一般没什么问题，但是一旦涉及到数据更新、数据库更新和缓存更新，就容易出现缓存和数据库间的一致性问题
 * 写库后更新缓存
 *        A更新库
 *        B更新库
 *        B更新缓存
 *        A更新缓存（脏数据）
 * 删缓存后更新库
 *        A删缓存
 *        B发现缓存没有了
 *        B请求数据库加入缓存（脏）
 *        A将新值入库
 * 更新库，删除缓存
 *        1、缓存失效
 *        2、A查询数据库
 *        3、B将新值入库
 *        4、B删除缓存
 *        5、A将旧值写入缓存
 *    但此种情形比较难出现，2比3块，所以5一般在4前面，此种情况是较为理想的，另外可以通过延迟删除缓存来达到更理想的装状态
 * 延时双删策略
 *        先删除缓存
 *        写库
 *        sleep500ms
 *        缓存
 *
 *
 */
@Component
public class QueueListener {

    @Reference
    private TbItemDubboService tbItemDubboService;

    @Reference
    private TbContentDubboService tbContentDubboService;

    @Reference
    private TbOrderDubboService tbOrderDubboService;

    @Autowired
    private RedisTemplate redisTemplate;

    //大广告缓存key
    @Value("${ego.bigad.redisKey}")
    private String bidAdKey;

    //商品缓存key + 商品id
    @Value("${ego.item.redisKey}")
    private String itemKey;

    //搜索模块url
    @Value("${ego.search.location}")
    private String searchLocation;

    //购物车模块url
    @Value("${ego.cart.location}")
    private String cartLocation;

    @Autowired
    private EgoMailSender egoMailSender;


    /**
     * 大广告缓存删除
     * @param object
     */
    //即使没有发送队列，启动receiver也会创建
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.content.deleteBigad}"), exchange = @Exchange(name = "amq.direct"))})
    public void content(Object object) {
        System.out.println("删除大广告redishuancun");
        redisTemplate.delete(bidAdKey);
    }


    /**
     * 新增商品加入redis，加入solr
     * @param id
     */
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.item.insertName}"), exchange = @Exchange(name = "amq.direct"))})
    public void insertItem(String id) {
        System.out.println("接收到新增id" + id);
        //新增商品加入solr
        Map<String, String> map = new HashMap<>();
        map.put("ids", id);
        HttpClientUtil.doGet(searchLocation + "insert", map);

        String[] ids = id.split(",");
        for (String idArr : ids) {
            String key = itemKey + idArr;
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

    /**
     * 删除商品redis缓存，删除solr中商品
     * @param id
     */
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(name = "${ego.rabbitmq.item.deleteName}"), exchange = @Exchange(name = "amq.direct"))})
    public void deleteItem(String id) {
        System.out.println("接收到删除id" + id);

        Map<String, String> map = new HashMap<>();
        map.put("ids", id);

        //从solr中删除商品
        HttpClientUtil.doGet(searchLocation + "delete", map);

        String[] ids = id.split(",");
        for (String idArr : ids) {
            String key = itemKey + idArr;
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
        try {
            //反序列化
            byte[] body = message.getBody();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(body));
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
        //向谁发送邮件
        egoMailSender.send("2608194130@qq.com",orderId);
    }

}
