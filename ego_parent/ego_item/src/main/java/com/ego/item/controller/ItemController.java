package com.ego.item.controller;

import com.ego.item.pojo.ItemCategoryNav;
import com.ego.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 跨域请求
     * portal页面中查询商品菜单
     * @return
     */
    @RequestMapping("rest/itemcat/all")
    @ResponseBody
    @CrossOrigin
    public ItemCategoryNav showItemCat(){
        return itemService.showItemCat();
    }


    /**
     * 显示商品详情页
     * @param id
     * @return
     */
    @RequestMapping("/item/{id}.html")
    public String showItem(@PathVariable long id, Model model){
        model.addAttribute("item",itemService.showItem(id));
        return "item";
    }

    /**
     * 显示商品描述
     * 延迟1s显示
     * @param id
     * @return
     */
    @RequestMapping("/item/desc/{id}.html")
    @ResponseBody
    public String showItemDesc(@PathVariable long id){
        return itemService.showItemDesc(id);
    }

    /**
     * 显示商品规格参数
     * @param id item商品id
     * @return
     */
    @RequestMapping("/item/param/{id}.html")
    @ResponseBody
    public String showItemParam(@PathVariable long id){
        return itemService.showItemParam(id);
    }

}
