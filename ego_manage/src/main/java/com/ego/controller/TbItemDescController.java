package com.ego.controller;

import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItemDesc;
import com.ego.service.TbItemDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TbItemDescController {
    @Autowired
    private TbItemDescService tbItemDescService;


    /**
     * 修改商品业务
     * 查询商品详细信息
     * @param id 商品id
     * @return EgoResult 其中data为DescItem
     */
    @RequestMapping("/rest/item/query/item/desc/{id}")
    @ResponseBody
    public EgoResult showDesc(@PathVariable long id){
        return tbItemDescService.selectById(id);
    }
}
