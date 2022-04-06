package com.ego.service.impl;

import com.ego.commons.pojo.EgoResult;
import com.ego.dubbo.service.TbItemDescDubboService;
import com.ego.pojo.TbItemDesc;
import com.ego.service.TbItemDescService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class TbItemDescServiceImpl implements TbItemDescService {
    @Reference
    private TbItemDescDubboService tbItemDescDubboService;
    @Override
    public EgoResult selectById(long id) {
        TbItemDesc tbItemDesc = tbItemDescDubboService.selectById(id);
        return EgoResult.ok(tbItemDesc);
    }
}
