package com.ego.service;

import com.ego.commons.pojo.EasyUITree;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbContentCategory;

import java.util.List;

public interface TbContentCategoryService {
    /**
     * 显示内容分类树状菜单
     * @param pid 父id
     * @return easyui tree
     */
    List<EasyUITree> showContentCategory(long pid);


    /**
     * 新增内容类目
     * @param tbContentCategory
     * @return EgoResult
     */
    EgoResult insert(TbContentCategory tbContentCategory);


    /**
     * 修改内容类目名称
     * @param tbContentCategory
     * @return EgoResult
     */
    EgoResult update(TbContentCategory tbContentCategory);


    /**
     * 删除内容类目
     * @param id
     * @return
     */
    EgoResult delete(long id);
}
