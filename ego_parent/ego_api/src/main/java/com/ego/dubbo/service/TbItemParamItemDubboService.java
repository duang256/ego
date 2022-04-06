package com.ego.dubbo.service;

import com.ego.pojo.TbItemParamItem;

public interface TbItemParamItemDubboService {
    /**
     * 根据商品id查询商品规格参数信息
     * @param itemId 商品id
     * @return 商品规格参数信息
     */
    TbItemParamItem selectByItemId(long itemId);
}
