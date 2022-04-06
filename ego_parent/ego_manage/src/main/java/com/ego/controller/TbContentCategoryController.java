package com.ego.controller;

import com.ego.commons.pojo.EasyUITree;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbContentCategory;
import com.ego.service.TbContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TbContentCategoryController {
    @Autowired
    private TbContentCategoryService tbContentCategoryService;


    /**
     * CMS内容分类
     * @param id 父id
     * @return
     */
    @RequestMapping("/content/category/list")
    public List<EasyUITree> showContentCategory(@RequestParam(defaultValue = "0") long id){
        return tbContentCategoryService.showContentCategory(id);
    }


    /**
     * 新增内容类目
     * @param tbContentCategory
     * @return
     */
    @RequestMapping("/content/category/create")
    public EgoResult insertContentCategory(TbContentCategory tbContentCategory){
        return tbContentCategoryService.insert(tbContentCategory);
    }

    /**
     * 内容类目重命名
     * @param tbContentCategory
     * @return
     */
    @RequestMapping("/content/category/update")
    public EgoResult updateContentCategory(TbContentCategory tbContentCategory){
        return tbContentCategoryService.update(tbContentCategory);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("/content/category/delete")
    public EgoResult deleteContentCategory(long id){
        return tbContentCategoryService.delete(id);
    }
}
