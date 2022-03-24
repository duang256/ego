package com.ego.service;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItem;

/**
 * 完成视图逻辑
 */
public interface TbItemService {
    /**
     * 分页显示商品信息
     * @param page 页码
     * @param rows 每页大小
     * @return 模板数据
     */
    EasyUIDatagrid showItem(int page,int rows);

    /**
     * 操作状态值
     * @param ids 批量id
     * @param status 状态
     * @return EgoResult状态对象
     */
    EgoResult updateStatus(long[] ids,int status);


    /**
     * 新增商品
     * @param tbItem 直接用TbItem接收
     * @param desc 因为前端传入参数和pojo不一致，所以这里用String接收了
     * @return EgoResult状态对象
     */
    EgoResult insertItem(TbItem tbItem,String desc,String itemParams);

    /**
     * 修改商品信息
     * @param tbItem 直接用TbItem接收
     * @param desc 因为前端传入参数和pojo不一致，所以这里用String接收了
     * @return EgoResult状态对象
     */
    EgoResult updateItem(TbItem tbItem,String desc,String itemParams,long itemParamId);
}
