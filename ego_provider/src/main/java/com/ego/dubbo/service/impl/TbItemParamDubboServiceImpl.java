package com.ego.dubbo.service.impl;

import com.ego.dubbo.service.TbItemParamDubboService;
import com.ego.mapper.TbItemParamMapper;
import com.ego.pojo.TbItemParam;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TbItemParamDubboServiceImpl implements TbItemParamDubboService {
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Override
    public List<TbItemParam> selectByPage(int pageNumber, int pageSize) {
        //设置分页参数
        PageHelper.startPage(pageNumber,pageSize);
        //如果查询结果包含text类型列，一定使用 withBlobs
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(null);
        PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
    }

    @Override
    public long selectCount() {
        return tbItemParamMapper.countByExample(null);
    }
}
