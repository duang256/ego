package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.service.TbItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController = Controller + 每个方法上面自动添加ResponseBody
 */
@RestController
public class TbItemParamController {
    @Autowired
    private TbItemParamService tbItemParamService;

    @RequestMapping("/item/param/list")
    public EasyUIDatagrid showItemParam(int page,int rows){
        return tbItemParamService.showItemParam(page,rows);
    }
}
