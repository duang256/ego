package com.ego.controller;

import com.ego.commons.pojo.EasyUITree;
import com.ego.service.TbItemCatService;
import com.ego.service.TbItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TbItemCatController {
    @Autowired
    private TbItemCatService tbItemService;


    /**
     * easyUI类目树
     * @param id 必须固定叫id 顶层菜单默认值为0
     * @return
     */
    @RequestMapping("/item/cat/list")
    @ResponseBody
    public List<EasyUITree> showTree(@RequestParam(defaultValue = "0") int id){
        return tbItemService.showTree(id);
    }
}
