package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbItem;

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
}
