package com.ego.commons.pojo;

import java.io.Serializable;

public class DeleteCartPojo implements Serializable {
    public static final Long serialVersionUID=1L;
    private long userId;
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
