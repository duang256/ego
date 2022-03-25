package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.pojo.TbContent;
import com.ego.service.TbContentService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TbContentServiceImpl implements TbContentService {
    @Reference
    private TbContentDubboService tbContentDubboService;
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
            return EgoResult.ok();
        }
        return EgoResult.err("新增内容失败");

    }

    @Override
    public EgoResult update(TbContent tbContent) {
        tbContent.setUpdated(new Date());
        int index = tbContentDubboService.update(tbContent);
        if(index == 1){
            return EgoResult.ok();
        }
        return EgoResult.err("修改内容失败");
    }

    @Override
    public EgoResult delete(long[] ids) {
        try {
            int index = tbContentDubboService.delete(ids);
            if(index == 1){
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("批量删除内容失败");
    }
}
