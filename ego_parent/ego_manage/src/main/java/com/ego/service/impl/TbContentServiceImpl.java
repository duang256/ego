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

    @Value("${ego.rabbitmq.content.deleteBigad}")
    private String deleteBigad;

    @Value("${ego.bigad.categoryId}")
    private long bigad;

    @Override
    public EasyUIDatagrid showContent(long categoryId, int page, int rows) {
        List<TbContent> list = tbContentDubboService.selectByPage(categoryId, page, rows);
        long total = tbContentDubboService.selectCountByCategoryId(categoryId);
        return new EasyUIDatagrid(list, total);
    }

    @Override
    public EgoResult insert(TbContent tbContent) {
        Date date = new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
        tbContent.setId(IDUtils.genItemId());

        //延时双删
        try {

            if (bigad == tbContent.getCategoryId()) {
                send.send(deleteBigad, "async");
            }

            int index = tbContentDubboService.insert(tbContent);
            if (index == 1) {
                //新增成功
                //如果是大广告，向rabbitmq发送消息同步缓存
                if (bigad == tbContent.getCategoryId()) {
                    Thread.sleep(500);
                    send.send(deleteBigad, "async");
                }
                return EgoResult.ok();
            }

        } catch (DaoException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return EgoResult.err("新增内容失败");
    }


    @Override
    public EgoResult update(TbContent tbContent) {
        tbContent.setUpdated(new Date());

        //延时双删
        try {
            //延时双删
            if (bigad == tbContent.getCategoryId()) {
                send.send(deleteBigad, "async");
            }
            int index = tbContentDubboService.update(tbContent);
            if (index == 1) {
                if (bigad == tbContent.getCategoryId()) {
                        Thread.sleep(500);
                    send.send(deleteBigad, "async");
                }
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return EgoResult.err("修改内容失败");
    }

    @Override
    public EgoResult delete(long[] ids) {
        boolean isbigad = false;
        for (long id : ids) {
            TbContent tbContent = tbContentDubboService.selectById(id);
            if (tbContent.getCategoryId() == bigad) {
                isbigad = true;
                break;
            }
        }

        try {
            if (isbigad) {
                send.send(deleteBigad, "async");
            }
            int index = tbContentDubboService.delete(ids);
            if (index == 1) {
                if (isbigad) {
                    Thread.sleep(500);
                    send.send(deleteBigad, "async");
                }
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return EgoResult.err("批量删除内容失败");
    }
}
