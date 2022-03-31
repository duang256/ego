package com.ego.passport.service;

import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbUser;

public interface PassportService {
    /**
     * 检查用户是否存在
     * email 用户名  phone
     * @param tbUser
     * @return
     */
    EgoResult check(TbUser tbUser);

    /**
     * 用户注册
     * @param tbUser
     * @return
     */
    EgoResult register(TbUser tbUser);

    /**
     * 登陆
     * @param tbUser
     * @return
     */
    EgoResult login(TbUser tbUser);
}
