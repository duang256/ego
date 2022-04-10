package com.ego.commons.pojo;

import com.ego.pojo.TbOrderItem;
import com.ego.pojo.TbOrderShipping;

import java.io.Serializable;
import java.util.List;

/**
 * 接受前端数据的包装类
 */
public class OrderPojo implements Serializable {
    public static final Long serialVersionUID=1L;
    // 付款价格
    private String payment;
    // 付款方式
    private Integer paymentType;

    //收货人信息
    private TbOrderShipping orderShipping;
    //商品信息
    private List<TbOrderItem> orderItems;

    public List<TbOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TbOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public TbOrderShipping getOrderShipping() {
        return orderShipping;
    }

    public void setOrderShipping(TbOrderShipping orderShipping) {
        this.orderShipping = orderShipping;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }
}
