package com.ego.service;

import com.ego.commons.pojo.EgoResult;

public interface TbItemParamItemService {
    /**
     * 根据商品id查询商品规格参数
     * @param itemId 商品id
     * @return EgoResult 中ItemParamItem作为data
     */
    EgoResult showItemParamItem(long itemId);
}
