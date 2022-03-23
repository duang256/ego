package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItem;
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

    /**
     * 新增商品
     * @param tbItem 商品信息
     * @param desc 商品描述
     * @return EgoResult
     */
    @RequestMapping("/item/save")
    @ResponseBody
    public EgoResult insert(TbItem tbItem, String desc){
        return tbItemService.insertItem(tbItem,desc);
    }

    @RequestMapping("/rest/item/update")
    @ResponseBody
    public EgoResult update(TbItem tbItem,String desc){
        return tbItemService.updateItem(tbItem,desc);
    }
}
