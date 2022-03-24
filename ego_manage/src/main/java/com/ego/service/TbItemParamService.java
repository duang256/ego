package com.ego.service;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItemParam;


public interface TbItemParamService {
    EasyUIDatagrid showItemParam(int page,int rows);

    /**
     * 根据类目id查询规格参数模板
     * @param catId 类目id
     * @return 规格参数信息
     */
    EgoResult showItemParamByCatId(long catId);


    /**
     * 新增类目规格参数模板
     * @param tbItemParam
     * @return EgoResult
     */
    EgoResult insert(TbItemParam tbItemParam);


    /**
     *  删除规格参数模板
     * @param ids 规格参数模板id
     * @return EgoResult
     */
    EgoResult delete(long[] ids);
}
