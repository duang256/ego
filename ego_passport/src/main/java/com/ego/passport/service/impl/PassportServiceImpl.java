package com.ego.passport.service.impl;

import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbUserDubboService;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class PassportServiceImpl implements PassportService{

    @Reference
    private TbUserDubboService tbUserDubboService;
    
    @Override
    public EgoResult check(TbUser tbUser) {
        TbUser user = tbUserDubboService.selectByUser(tbUser);
        if(user == null){
            //没有查出对象，成功
            return EgoResult.ok();
        }
        return EgoResult.err("已经存在");
        
    }

    @Override
    public EgoResult register(TbUser tbUser) {
        Date date = new Date();
        tbUser.setId(IDUtils.genItemId());
        tbUser.setCreated(date);
        tbUser.setUpdated(date);
        //密码进行加密，在企业开发中，提供基础框架的项目经理一定会提供一个工具类用于密码加密
        //这里直接使用spring Framework 工具类
        String pwdMd = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(pwdMd);
        int insert = tbUserDubboService.insert(tbUser);
        if(insert == 1){
            return EgoResult.ok();
        }
        return EgoResult.err("注册失败");
    }

    @Override
    public EgoResult login(TbUser tbUser) {
        //密码进行加密，在企业开发中，提供基础框架的项目经理一定会提供一个工具类用于密码加密
        //这里直接使用spring Framework 工具类
        String pwdMd = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(pwdMd);
        TbUser user = tbUserDubboService.selectByUsernamePwd(tbUser);
        if(user != null){
            //这里需要放入user放入EgoResult中，控制器需要把用户信息放到作用域中
            return EgoResult.ok(user);
        }
        return EgoResult.err("账号或密码错误");
    }
}
