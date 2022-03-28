package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.pojo.TbContent;
import com.ego.sender.Send;
import com.ego.service.TbContentService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TbContentServiceImpl implements TbContentService {
    @Reference
    private TbContentDubboService tbContentDubboService;
    @Autowired
    private Send send;

    @Value("${ego.rabbitmq.content.queuename}")
    private String queuename;

    @Value("${ego.bigad.categoryId}")
    private long bigad;

    @Override
    public EasyUIDatagrid showContent(long categoryId, int page, int rows) {
        List<TbContent> list = tbContentDubboService.selectByPage(categoryId,page, rows);
        long total = tbContentDubboService.selectCountByCategoryId(categoryId);
        return new EasyUIDatagrid(list,total);
    }

    @Override
    public EgoResult insert(TbContent tbContent) {
        Date date = new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
        tbContent.setId(IDUtils.genItemId());
        int index = tbContentDubboService.insert(tbContent);
        if(index == 1){
            //新增成功
            //向rabbitmq发送消息同步缓存
            if(bigad == tbContent.getCategoryId()) {
                send.send(queuename, "async");
            }
            return EgoResult.ok();
        }
        return EgoResult.err("新增内容失败");

    }

    @Override
    public EgoResult update(TbContent tbContent) {
        tbContent.setUpdated(new Date());
        int index = tbContentDubboService.update(tbContent);
        if(index == 1){
            if(bigad == tbContent.getCategoryId()) {
                send.send(queuename, "async");
            }
            return EgoResult.ok();
        }
        return EgoResult.err("修改内容失败");
    }

    @Override
    public EgoResult delete(long[] ids) {
        boolean isbigad = false;
        for(long id : ids){
            TbContent tbContent = tbContentDubboService.selectById(id);
            if(tbContent.getCategoryId() == bigad){
                isbigad = true;
                break;
            }
        }

        try {
            int index = tbContentDubboService.delete(ids);
            if(index == 1){
                //由于删除需要额外的dubbo查询categoryid后进行判断categoryid并进行异步缓存同步
                // 此查询过程会影响到正常业务的速度，所以不能写在此处
                if(isbigad){
                    send.send(queuename, "async");
                }
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("批量删除内容失败");
    }
}
