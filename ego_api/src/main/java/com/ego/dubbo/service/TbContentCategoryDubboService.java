package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbContentCategory;

import java.util.List;

public interface TbContentCategoryDubboService {
    /**
     * 根据父id返回所有子类目
     * @param pid 父id
     * @return 子类目List
     */
    List<TbContentCategory> selectByPid(long pid);


    /**
     * 新增
     * @param tbContentCategory
     * @return id
     */
    int insert(TbContentCategory tbContentCategory);


    /**
     * 修改
     * @param tbContentCategory
     * @return
     */
    int updateNameById(TbContentCategory tbContentCategory);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteById(long id) throws DaoException;
}
