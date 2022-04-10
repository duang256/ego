package com.ego.commons.pojo;

import java.io.Serializable;

/**
 * 下订单时异步删除购物车商品
 * 此类为传递给rabbitmq的pojo类
 */
public class DeleteCartPojo implements Serializable {
    public static final Long serialVersionUID=1L;
    //用户id
    private long userId;
    //商品id，逗号分隔多个id
    private String itemIds;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getItemIds() {
        return itemIds;
    }

    public void setItemIds(String itemIds) {
        this.itemIds = itemIds;
    }
}
