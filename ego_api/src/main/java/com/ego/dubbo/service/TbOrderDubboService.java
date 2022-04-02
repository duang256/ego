package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbOrder;
import com.ego.pojo.TbOrderItem;
import com.ego.pojo.TbOrderShipping;

import java.util.List;

public interface TbOrderDubboService {
    /**
     * 新增一条订单数据
     * @param tbOrder
     * @param list
     * @param tbOrderShipping
     * @return
     */
    int insert(TbOrder tbOrder, List<TbOrderItem> list, TbOrderShipping tbOrderShipping) throws DaoException ;
}
