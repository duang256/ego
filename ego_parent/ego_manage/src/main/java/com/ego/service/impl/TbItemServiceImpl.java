package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
import com.ego.sender.Send;
import com.ego.service.TbItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TbItemServiceImpl implements TbItemService {
    @Reference
    private TbItemDubboService tbItemDubboService;

    @Autowired
    private Send send;

    @Value("${ego.rabbitmq.item.insertName}")
    private String insertName;

    @Value("${ego.rabbitmq.item.deleteName}")
    private String deleteName;

    @Override
    public EasyUIDatagrid showItem(int page, int rows) {
        List<TbItem> list = tbItemDubboService.selectByPage(rows, page);
        long total = tbItemDubboService.selectCount();
        return new EasyUIDatagrid(list,total);
    }

    @Override
    public EgoResult updateStatus(long[] ids, int status) {
        try {
            int index = tbItemDubboService.updateStatusByIds(ids, status);
            if(index == 1) {
                if(status == 1) {
                    //上架
                    //将ids数组转换为String，逗号分隔
                    send.send(insertName, StringUtils.join(ids,','));
                }else if(status == 2 || status == 3){
                    //下架或删除
                    send.send(deleteName, StringUtils.join(ids,','));
                }
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("操作失败");
    }

    @Override
    public EgoResult insertItem(TbItem tbItem, String desc,String itemParams) {
        Date date = new Date();
        long id = IDUtils.genItemId();
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        tbItem.setStatus((byte) 1);
        //分布式项目中表主键都不是自增的，而是通过算法生成的
        tbItem.setId(id);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemId(id);
        tbItemDesc.setItemDesc(desc);

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setId(IDUtils.genItemId());
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setItemId(id);
        tbItemParamItem.setParamData(itemParams);
        try {
            int index = tbItemDubboService.insert(tbItem, tbItemDesc,tbItemParamItem);
            if(index == 1){
                //对solr进行同步
                send.send(insertName,id);
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("商品新增失败");
    }

    @Override
    public EgoResult updateItem(TbItem tbItem, String desc,String itemParams,long itemParamId) {
        Date date = new Date();
        tbItem.setUpdated(date);
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setItemId(tbItem.getId());

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setParamData(itemParams);
        //这里id为模板参数主键id
        tbItemParamItem.setId(itemParamId);
        tbItemParamItem.setUpdated(date);

        try {
            int index = tbItemDubboService.update(tbItem, tbItemDesc,tbItemParamItem);
            if(index == 1){
                send.send(insertName,tbItem.getId());
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("商品信息修改失败");
    }
}
