package com.ego.service;

import com.ego.commons.pojo.EasyUITree;

import java.util.List;

public interface TbItemCatService {
    /**
     * 显示树状菜单
     * @param pid 父id
     * @return 所有子菜单
     */
    List<EasyUITree> showTree(long pid);
}
