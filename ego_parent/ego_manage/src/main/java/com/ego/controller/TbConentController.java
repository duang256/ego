package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbContent;
import com.ego.service.TbContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TbConentController {

    @Autowired
    private TbContentService tbContentService;

    /**
     * 内容分页
     *
     * @param categoryId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/content/query/list")
    public EasyUIDatagrid showContent(long categoryId, int page, int rows) {
        return tbContentService.showContent(categoryId, page, rows);
    }


    /**
     * 新增内容
     *
     * @param tbContent
     * @return
     */
    @RequestMapping("/content/save")
    public EgoResult insertContent(TbContent tbContent) {
        return tbContentService.insert(tbContent);
    }


    /**
     * 修改内容
     *
     * @param tbContent
     * @return
     */
    @RequestMapping("/rest/content/edit")
    public EgoResult updateContent(TbContent tbContent) {
        return tbContentService.update(tbContent);
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/content/delete")
    public EgoResult deleteContent(long[] ids) {
        return tbContentService.delete(ids);
    }
}
