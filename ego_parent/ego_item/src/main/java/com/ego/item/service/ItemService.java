package com.ego.item.service;

import com.ego.commons.pojo.TbItemDetails;
import com.ego.item.pojo.ItemCategoryNav;

public interface ItemService {
    /**
     * 导航栏菜单
     * @return
     */
    ItemCategoryNav showItemCat();

    /**
     * 显示商品详情
     * @param id 商品id
     * @return
     */
    TbItemDetails showItem(long id);


    /**
     * 显示商品描述
     * @param id
     * @return
     */
    String showItemDesc(long id);

    /**
     * 由于前端页面直接将返回数据放到了div中
     * 所以需要进行拼接
     */

    String showItemParam(long itemId);

}
