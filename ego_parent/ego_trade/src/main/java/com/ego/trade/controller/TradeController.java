package com.ego.trade.controller;

import com.ego.commons.pojo.OrderPojo;
import com.ego.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 库存问题：并发问题需要使用RabbitMQ进行队列消息排序创建订单      创建订单是三表新增   tb_order(订单信息)  tb_order_item（商品信息）   tb_order_shipping（收货人信息）
 * 购物车商品问题：当订单创建成功后需要删除购物车对应商品
 * 发邮件问题：订单创建成功后给用户发邮件
 */
@Controller
public class TradeController {

    @Autowired
    private TradeService tradeService;
    @RequestMapping("/order/create.html")
    public String createOrder(OrderPojo orderPojo, Model model){
        Map<String, Object> result = tradeService.createOrder(orderPojo);
        if(result != null){
            model.addAllAttributes(result);
            return "success";
        }
        model.addAttribute("message","订单创建失败");
        return "error/exception";
    }
}

