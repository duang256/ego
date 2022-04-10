package com.ego.passport.service.impl;

import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.IDUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.commons.utils.ServletUtil;
import com.ego.dubbo.service.TbUserDubboService;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
public class PassportServiceImpl implements PassportService{

    @Reference
    private TbUserDubboService tbUserDubboService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${ego.cart.redisKey}")
    private String  cartRedisKey;

    @Value("${ego.cart.tempcart}")
    private String tempcartKey;
    
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
            /*
            登录时临时购物车合并到redis中
             */

            //获取cookie临时购物车信息
            String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(),tempcartKey);
            Map<Long, CartPojo> map = new HashMap<>();
            if(Strings.isNotEmpty(cookieValue)) {
                map =  JsonUtils.jsonToMap(cookieValue, Long.class, CartPojo.class);
            }

            //redis用户购物车信息，需要处理空指针，如果没有就创建一个
            String key = cartRedisKey + user.getId();
            List<CartPojo> list = (List<CartPojo>)redisTemplate.opsForValue().get(key);


            if(list != null){
                for (long id : map.keySet()) {  //临时购物车
                    boolean isExists = false;
                    for (CartPojo cart : list) {  //用户购物车
                        if (id == cart.getId()) {
                            cart.setNum(map.get(id).getNum());
                            isExists = true;
                            break;
                        }
                    }
                    if (!isExists) {
                        list.add(map.get(id));
                    }
                }
                redisTemplate.opsForValue().set(key, list);
            }else {
                list = new ArrayList<>();
                for(long id : map.keySet()){
                    list.add(map.get(id));
                }
                redisTemplate.opsForValue().set(key, list);
            }

            //删除临时购物车
            CookieUtils.deleteCookie(ServletUtil.getRequest(), ServletUtil.getResponse(), tempcartKey);

            //这里需要放入user放入EgoResult中，控制器需要把用户信息放到作用域中
            return EgoResult.ok(user);
        }
        return EgoResult.err("账号或密码错误");
    }
}
