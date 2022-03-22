package com.ego.dubbo.service;

import com.ego.pojo.Manager;

public interface ManagerDubboService {
    /**
     * 根据用户名查询后台用户信息
     * @param username 用户名
     * @return 用户详情
     */
    Manager selectManagerByUsername(String username);
}
