package com.ego.dubbo.service;

import com.ego.pojo.TbItemParam;

import java.util.List;

public interface TbItemParamDubboService {
    /**
     * 分页查询
     * @param pageNumber 第几页
     * @param pageSize 每页大小
     * @return 当前页数据
     */
    List<TbItemParam> selectByPage(int pageNumber ,int pageSize);

    /**
     * 查询总数量
     * @return
     */
    long selectCount();
}
