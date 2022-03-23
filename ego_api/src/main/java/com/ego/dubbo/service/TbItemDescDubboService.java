package com.ego.dubbo.service;

import com.ego.pojo.TbItemDesc;

public interface TbItemDescDubboService {
    /**
     * 根据主键查询对象信息
     * @param id
     * @return
     */
    TbItemDesc selectById(long id);
}
