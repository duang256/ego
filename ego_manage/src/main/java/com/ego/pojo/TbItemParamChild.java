package com.ego.pojo;

import com.ego.dubbo.service.TbItemParamDubboService;

/**
 * 页面商品类目包装类
 */
public class TbItemParamChild extends TbItemParam {
    private String itemCatName;


    public String getItemCatName() {
        return itemCatName;
    }

    public void setItemCatName(String itemCatName) {
        this.itemCatName = itemCatName;
    }
}
