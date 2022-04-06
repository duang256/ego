package com.ego.search.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
    /**
     * 实现solr数据查询
     * @param q
     * @return
     */
    Map<String,Object> search(String q,int page,int size);

    /**
     * item商品新增
     * @param ids
     * @return
     */
    int insert(long[] ids);


    /**
     * solr商品批量删除
     * @param ids
     * @return
     */
    int delete(String[] ids);
}
