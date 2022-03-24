package com.ego.service.impl;

import com.ego.commons.pojo.EgoResult;
import com.ego.dubbo.service.TbItemParamItemDubboService;
import com.ego.pojo.TbItemParamItem;
import com.ego.service.TbItemParamItemService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class TbItemParamItemServiceImpl implements TbItemParamItemService {
    @Reference
    private TbItemParamItemDubboService tbItemParamItemDubboService;
    @Override
    public EgoResult showItemParamItem(long itemId) {
        TbItemParamItem tbItemParamItem = tbItemParamItemDubboService.selectByItemId(itemId);
        if(tbItemParamItem != null){
            return EgoResult.ok(tbItemParamItem);
        }
        return EgoResult.err("查询模板参数失败");
    }
}
