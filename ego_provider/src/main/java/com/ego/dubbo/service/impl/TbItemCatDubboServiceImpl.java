package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.mapper.TbItemCatMapper;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemCat;
import com.ego.pojo.TbItemCatExample;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TbItemCatDubboServiceImpl implements TbItemCatDubboService {
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Override
    public List<TbItemCat> selectByPid(long pid) {
        TbItemCatExample example = new TbItemCatExample();
        example.createCriteria().andParentIdEqualTo(pid);
        return tbItemCatMapper.selectByExample(example);
    }
}
