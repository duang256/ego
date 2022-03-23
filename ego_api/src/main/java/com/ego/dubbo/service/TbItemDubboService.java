package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;

import java.util.List;

public interface TbItemDubboService {
    /**
     * 分页查询
     * @param pageSize 每页大小
     * @param pageNumber  页码
     * @return 当前页显示数据
     */
    List<TbItem> selectByPage(int pageSize,int pageNumber);

    /**
     * 查询总条数
     * @return 总条数
     */
    long selectCount();

    /**
     * 事务一定要写在provider方
     * 更新商品状态信息
     * @param ids 批量id
     * @param status 修改的状态值
     * @return 成功1，失败0
     */
    int updateStatusByIds(long[] ids,int status) throws DaoException;


    /**
     * 商品新增功能
     * @param tbItem 商品信息
     * @param tbItemDesc 商品描述信息 放到tbItemDesc表中
     * @return 成功1，失败0
     */
    int insert(TbItem tbItem, TbItemDesc tbItemDesc) throws  DaoException;

    /**
     * 商品修改
     * @param tbItem 商品信息
     * @param tbItemDesc 商品描述信息 放到tbItemDesc表中
     * @return 成功1，失败0
     * @throws DaoException
     */
    int update(TbItem tbItem,TbItemDesc tbItemDesc) throws  DaoException;
}
