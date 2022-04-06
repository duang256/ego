package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItemParam;
import com.ego.service.TbItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    public EasyUIDatagrid showItemParam(int page, int rows) {
        return tbItemParamService.showItemParam(page, rows);
    }

    /**
     * 根据类目id查询规格模板
     * @param id 类目id
     * @return EgoResult 中data为TbItemParam
     */
    @RequestMapping("/item/param/query/itemcatid/{id}")
    public EgoResult showItemParamByCatId(@PathVariable long id) {
        return tbItemParamService.showItemParamByCatId(id);
    }

    /**
     * 新增类目的规格模板
     * @param tbItemParam
     * @param catId
     * @return
     */
    @RequestMapping("/item/param/save/{catId}")
    public EgoResult insert(TbItemParam tbItemParam,@PathVariable long catId){
        tbItemParam.setItemCatId(catId);
        return tbItemParamService.insert(tbItemParam);
    }

    /**
     * 批量删除类目的规格模板
     * @param ids 规格模板id
     * @return EgoResult
     */
    @RequestMapping("/item/param/delete")
    public EgoResult delete(long[] ids){
       return  tbItemParamService.delete(ids);
    }


}
