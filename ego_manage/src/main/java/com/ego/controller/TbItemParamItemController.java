package com.ego.controller;

import com.ego.commons.pojo.EgoResult;
import com.ego.service.TbItemParamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TbItemParamItemController {
    @Autowired
    private TbItemParamItemService tbItemParamItemService;

    /**
     * 根据商品id查询模板参数数据
     * @param itemId 商品id
     * @return
     */
    @RequestMapping("/rest/item/param/item/query/{itemId}")
    @ResponseBody
    public EgoResult showItemParamItemByItemId( @PathVariable long itemId){
        return tbItemParamItemService.showItemParamItem(itemId);
    }
}
