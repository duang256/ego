package com.ego.dubbo.service.impl;

import com.ego.dubbo.service.TbItemDescDubboService;
import com.ego.mapper.TbItemDescMapper;
import com.ego.pojo.TbItemDesc;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TbItemDescDubboServiceImpl implements TbItemDescDubboService {
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Override
    public TbItemDesc selectById(long id) {
        return tbItemDescMapper.selectByPrimaryKey(id);
    }
}
