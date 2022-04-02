package com.ego.trade.service;


import com.ego.commons.pojo.OrderPojo;

import java.util.Map;

public interface TradeService {
    Map<String,Object> createOrder(OrderPojo orderPojo);
}
