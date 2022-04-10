package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbOrderDubboService;
import com.ego.mapper.TbItemMapper;
import com.ego.mapper.TbOrderItemMapper;
import com.ego.mapper.TbOrderMapper;
import com.ego.mapper.TbOrderShippingMapper;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbOrder;
import com.ego.pojo.TbOrderItem;
import com.ego.pojo.TbOrderShipping;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TbOrderDubboServiceImpl implements TbOrderDubboService {
    @Autowired
    private TbOrderMapper tbOrderMapper;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    @Transactional
    public int insert(TbOrder tbOrder, List<TbOrderItem> list, TbOrderShipping tbOrderShipping) throws DaoException{
        int index = tbOrderMapper.insertSelective(tbOrder);
        if(index == 1){
            int itemIndex = 0;
            int updateItemNum = 0;
            for (TbOrderItem item: list) {
                itemIndex += tbOrderItemMapper.insert(item);
                //修改商品库存
                long itemId = Long.parseLong(item.getItemId());
                TbItem tbItemDB = tbItemMapper.selectByPrimaryKey(itemId);
                TbItem tbItem = new TbItem();
                tbItem.setId(itemId);
                tbItem.setNum(tbItemDB.getNum() - item.getNum());
                tbItem.setUpdated(tbOrder.getCreateTime());
                updateItemNum += tbItemMapper.updateByPrimaryKeySelective(tbItem);
            }
            if(itemIndex == list.size() && updateItemNum == list.size()){
                int shippingIndex = tbOrderShippingMapper.insertSelective(tbOrderShipping);
                if(shippingIndex == 1){
                    return 1;
                }

            }
        }
        throw new DaoException("订单新增失败");
    }
}
