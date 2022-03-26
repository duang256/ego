package com.ego.item.controller;

import com.ego.item.pojo.ItemCategoryNav;
import com.ego.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("rest/itemcat/all")
    @ResponseBody
    @CrossOrigin
    public ItemCategoryNav showItemCat(){
        return itemService.showItemCat();
    }
}
