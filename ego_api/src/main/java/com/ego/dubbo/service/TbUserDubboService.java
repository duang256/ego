package com.ego.dubbo.service;

import com.ego.pojo.TbUser;

public interface TbUserDubboService {
    /**
     * 动态sql
     * @param user
     * @return
     */
    TbUser selectByUser(TbUser user);

    /**
     * user新增
     * @param tbUser
     * @return
     */
    int insert(TbUser tbUser);


    /**
     * 根据用户名和密码查询
     * @param tbUser
     * @return
     */
    TbUser selectByUsernamePwd(TbUser tbUser);
}
