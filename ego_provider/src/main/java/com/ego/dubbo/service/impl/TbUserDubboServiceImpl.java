package com.ego.dubbo.service.impl;

import com.ego.dubbo.service.TbUserDubboService;
import com.ego.mapper.TbUserMapper;
import com.ego.pojo.TbUser;
import com.ego.pojo.TbUserExample;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TbUserDubboServiceImpl implements TbUserDubboService {
    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public TbUser selectByUser(TbUser user) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        if(user.getUsername() != null){
            //username传值了
            criteria.andUsernameEqualTo(user.getUsername());
        } else if(user.getEmail() != null){
            //email传值了
            criteria.andEmailEqualTo(user.getEmail());
        } else if(user.getPhone() != null){
            criteria.andPhoneEqualTo(user.getPhone());
        }
        List<TbUser> list = tbUserMapper.selectByExample(tbUserExample);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;

    }

    @Override
    public int insert(TbUser tbUser) {
        return  tbUserMapper.insert(tbUser);
    }

    @Override
    public TbUser selectByUsernamePwd(TbUser tbUser) {
        TbUserExample example = new TbUserExample();
        //此处应为加密后的密码
        example.createCriteria().andUsernameEqualTo(tbUser.getUsername()).andPasswordEqualTo(tbUser.getPassword());
        List<TbUser> list = tbUserMapper.selectByExample(example);
        if(list != null && list.size() > 0) return list.get(0);
        return null;
    }
}
