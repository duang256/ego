package com.ego.trade.service;


import com.ego.commons.pojo.OrderPojo;

import java.util.Map;

public interface TradeService {
    /**
     * 创建订单
     * @param orderPojo
     * @return
     */
    Map<String,Object> createOrder(OrderPojo orderPojo);
}
