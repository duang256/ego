package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUITree;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbContentCategoryDubboService;
import com.ego.pojo.TbContentCategory;
import com.ego.service.TbContentCategoryService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TbContentCategoryServiceImpl implements TbContentCategoryService {
    @Reference
    private TbContentCategoryDubboService tbContentCategoryDubboService;

    @Override
    public List<EasyUITree> showContentCategory(long pid) {
        List<TbContentCategory> list = tbContentCategoryDubboService.selectByPid(pid);
        //内容转换，需要返回EasyUITree
        List<EasyUITree> treeList = new ArrayList();
        for(TbContentCategory contentCategory : list){
            EasyUITree tree = new EasyUITree();
            tree.setId(contentCategory.getId());
            //父类目close 子类目open
            tree.setState(contentCategory.getIsParent() ? "closed" : "open");
            tree.setText(contentCategory.getName());
            treeList.add(tree);
        }
        return treeList;
    }

    @Override
    public EgoResult insert(TbContentCategory tbContentCategory) {
        Date date = new Date();
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(date);
        tbContentCategory.setUpdated(date);
        tbContentCategory.setId(IDUtils.genItemId());
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setStatus(1);
        try {
            int index = tbContentCategoryDubboService.insert(tbContentCategory);
            if(index == 1){
                return EgoResult.ok(tbContentCategory);
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("创建内容类目失败");
    }

    @Override
    public EgoResult update(TbContentCategory tbContentCategory) {
        tbContentCategory.setUpdated(new Date());
        int index = tbContentCategoryDubboService.updateNameById(tbContentCategory);
        if(index == 1){
            return EgoResult.ok();
        }
        return EgoResult.err("修改内容类目名称失败");
    }

    @Override
    public EgoResult delete(long id) {
        try {
            int index = tbContentCategoryDubboService.deleteById(id);
            if(index == 1){
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.err("删除内容类目失败");

    }
}
