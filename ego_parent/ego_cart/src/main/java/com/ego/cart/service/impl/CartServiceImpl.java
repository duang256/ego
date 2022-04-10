package com.ego.cart.service.impl;

import com.ego.cart.service.CartService;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.commons.utils.ServletUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${ego.item.redisKey}")
    private String itemRedisKey;

    @Value("${ego.cart.tempcart}")
    private String tempcartKey;

    /**
     * 加入购物车按钮在商品详情页面
     * 此时商品详情一定已经缓存进redis
     * 所以不用查询mysql直接查询redis
     * cacheNames = "com.ego.item",key = "'showItem:' + #id"
     * @param id
     * @param num
     */
    @Override
    public void addCart(long id, int num) {
        //cookie取值 Base64解码，用于去除特殊字符，因为cookie对特殊字符支持不好
        String cookie = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempcartKey);

        //cookie中有数据
        if(Strings.isNotEmpty(cookie)){
            //工具类中id是转为String，取得时候需要用String取
            Map<Long,CartPojo> map = JsonUtils.jsonToMap(cookie,Long.class,CartPojo.class);
            if(map.containsKey(id)){
                //如果存在，只需要修改商品数量集合
                CartPojo cartPojo = map.get(id);
                cartPojo.setNum(cartPojo.getNum() + num);
            }else{
                //如果不存在，向cookie中加入新数据
                map.put(id,addItem(id, num));
            }
            CookieUtils.doSetCookieBase64(ServletUtil.getRequest(),ServletUtil.getResponse(),tempcartKey, JsonUtils.objectToJson(map),25920000); //一个月过期时间
            return;
        }

        //cookie中没有商品
        Map<Long,CartPojo> tempCart = new HashMap<>();
        tempCart.put(id,addItem(id, num));
        CookieUtils.doSetCookieBase64(ServletUtil.getRequest(),ServletUtil.getResponse(),tempcartKey, JsonUtils.objectToJson(tempCart),25920000); //一个月过期时间

    }

    public CartPojo addItem(long id, int num){
        //redis中商品key 软编码
        String key = itemRedisKey + id;
        TbItemDetails tbItemDetails = (TbItemDetails)redisTemplate.opsForValue().get(key);
        CartPojo cartPojo = new CartPojo();
        cartPojo.setId(tbItemDetails.getId());
        cartPojo.setImages(tbItemDetails.getImages());
        cartPojo.setId(tbItemDetails.getId());
        cartPojo.setNum(num);
        cartPojo.setPrice(tbItemDetails.getPrice());
        cartPojo.setTitle(tbItemDetails.getTitle());
        return cartPojo;
    }


    @Override
    public List<CartPojo> showCart(){
        List<CartPojo> list = new ArrayList<>();
        //临时购物车
        String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(),tempcartKey);
        if(Strings.isNotEmpty(cookieValue)){
            Map<Long,CartPojo> map = JsonUtils.jsonToMap(cookieValue,Long.class,CartPojo.class);
            for(long id : map.keySet()){
                list.add(map.get(id));
            }
        }
        return list;
    }

    @Override
    public EgoResult updateNum(long id, int num) {
        try {
            String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(),tempcartKey);
            Map<Long,CartPojo> map = JsonUtils.jsonToMap(cookieValue,Long.class,CartPojo.class);
            map.get(id).setNum(num);
            CookieUtils.doSetCookieBase64(ServletUtil.getRequest(),ServletUtil.getResponse(),tempcartKey, JsonUtils.objectToJson(map),25920000); //一个月过期时间
            return EgoResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EgoResult.err("更新数量出错");
    }

    @Override
    public EgoResult delete(long id) {
        try {
            String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(),tempcartKey);
            Map<Long,CartPojo> map = JsonUtils.jsonToMap(cookieValue,Long.class,CartPojo.class);
            map.remove(id);
            CookieUtils.doSetCookieBase64(ServletUtil.getRequest(),ServletUtil.getResponse(),tempcartKey, JsonUtils.objectToJson(map),25920000); //一个月过期时间
            return EgoResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EgoResult.err("删除出错");
    }

}
