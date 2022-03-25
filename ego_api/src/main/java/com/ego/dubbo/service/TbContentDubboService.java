package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbContent;

import java.util.List;

public interface TbContentDubboService {
    /**
     * 根据类目分页
     * @param categoryId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    List<TbContent> selectByPage(long categoryId , int pageNumber , int pageSize);

    /**
     * 根据类目查询数量
     * @return
     */
    long selectCountByCategoryId(long categoryId);


    /**
     * 新增
     * @param tbContent
     * @return
     */
    int insert(TbContent tbContent);


    /**
     * 修改
     * @param tbContent
     * @return
     */
    int update(TbContent tbContent);

    /**
     * 根据内容id批量删除
     * @param ids
     * @return
     */
    int delete(long[] ids) throws DaoException;
}
