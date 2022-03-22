package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.service.TbItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TbItemController {
    @Autowired
    private TbItemService tbItemService;

    /**
     * 查询
     * @param page
     * @param rows
     * @return EasyUIDatagrid分页数据
     */
    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDatagrid showItem(int page,int rows){
        return tbItemService.showItem(page,rows);
    }

    /**
     * 删除 状态为3
     * @param ids 批量id
     * @return EgoResult
     */
    @RequestMapping("/rest/item/delete")
    @ResponseBody
    public EgoResult delete(long[] ids){
        return tbItemService.updateStatus(ids, 3);
    }

    /**
     * 上架 状态为1
     * @param ids 批量id
     * @return EgoResult
     */
    @RequestMapping("/rest/item/reshelf")
    @ResponseBody
    public EgoResult reshelf(long[] ids){
        return tbItemService.updateStatus(ids, 1);
    }

    /**
     * 下架 状态为2
     * @param ids 批量id
     * @return EgoResult
     */
    @RequestMapping("/rest/item/instock")
    @ResponseBody
    public EgoResult instock(long[] ids){
        return tbItemService.updateStatus(ids, 2);
    }

}
