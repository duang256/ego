package com.ego.service.impl;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.dubbo.service.TbItemParamDubboService;
import com.ego.pojo.TbItemCat;
import com.ego.pojo.TbItemParam;
import com.ego.pojo.TbItemParamChild;
import com.ego.service.TbItemParamService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TbItemParamServiceImpl implements TbItemParamService {
    @Reference
    private TbItemParamDubboService tbItemParamDubboService;

    @Reference
    private TbItemCatDubboService tbItemCatDubboService;
    @Override
    public EasyUIDatagrid showItemParam(int page, int rows) {
        List<TbItemParam> list = tbItemParamDubboService.selectByPage(page, rows);
        List<TbItemParamChild> listChild = new ArrayList<>();
        //循环遍历获取每个类目的名字
        for(TbItemParam tbItemParam : list){
            TbItemParamChild child = new TbItemParamChild();
            child.setId(tbItemParam.getId());
            //赋值同名属性，将tbItemParam中的同名属性赋值给child
            BeanUtils.copyProperties(tbItemParam,child);
            child.setItemCatName(tbItemCatDubboService.selectById(tbItemParam.getItemCatId()).getName());
            listChild.add(child);
        }
        long total = tbItemParamDubboService.selectCount();
        return new EasyUIDatagrid(listChild,total);
    }
}
