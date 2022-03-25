package com.ego.service;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbContent;

public interface TbContentService {
    EasyUIDatagrid showContent(long categoryId,int page,int rows);
    EgoResult insert(TbContent tbContent);
    EgoResult update(TbContent tbContent);
    EgoResult delete(long[] ids);
}
