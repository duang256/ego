package com.ego.cart.service.impl;

import com.ego.cart.pojo.OrderCartPojo;
import com.ego.cart.service.CartService;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.commons.utils.ServletUtil;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbUser;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
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

    @Value("${ego.cart.redisKey}")
    private String  cartRedisKey;

    @Reference
    private TbItemDubboService tbItemDubboService;


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
        TbUser user = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");

        if(user != null){
            List<CartPojo> list = new ArrayList<>();
            //购物车存储到Redis中
            //设定redis中 key = cart:用户id，同样进行软编码
            String key = cartRedisKey + user.getId();

            if(redisTemplate.hasKey(key)) {
                list = (List<CartPojo>)redisTemplate.opsForValue().get(key);
                //判断当前商品是否存在，如果存在，修改数量
                for(CartPojo cart : list){
                    if(cart.getId() == id){
                        cart.setNum(cart.getNum() + num);
                        redisTemplate.opsForValue().set(key,list);
                        return ;
                    }
                }
            }
            //第一次存储redis和购物车无此商品 需要新增 都走这两行代码
            list.add(addItem(id, num));
            redisTemplate.opsForValue().set(key,list);
            return ;
        }

        //cookie取值 Base64解码，用于去除特殊字符，因为cookie对特殊字符支持不好
        String cookie = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempcartKey);
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
        TbUser user = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");
        List<CartPojo> list = new ArrayList<>();
        if(user != null){
            //用户购物车
            list =  (List<CartPojo>)redisTemplate.opsForValue().get(cartRedisKey + user.getId());
            return list;
        }

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
        TbUser user = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");
        if(user != null){
            String key = cartRedisKey + user.getId();
            //用户购物车
            List<CartPojo> list  =  (List<CartPojo>)redisTemplate.opsForValue().get(cartRedisKey + user.getId());
            for(CartPojo cart : list){
                if(id == cart.getId()){
                    cart.setNum(num);
                    redisTemplate.opsForValue().set(key,list);
                    return EgoResult.ok();
                }
            }
        }



        String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(),tempcartKey);
        Map<Long,CartPojo> map = JsonUtils.jsonToMap(cookieValue,Long.class,CartPojo.class);
        map.get(id).setNum(num);
        CookieUtils.doSetCookieBase64(ServletUtil.getRequest(),ServletUtil.getResponse(),tempcartKey, JsonUtils.objectToJson(map),25920000); //一个月过期时间
        return EgoResult.ok();
    }

    @Override
    public EgoResult delete(long id) {
        TbUser user = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");
        if(user != null){
            String key = cartRedisKey + user.getId();
            //用户购物车
            List<CartPojo> list  =  (List<CartPojo>)redisTemplate.opsForValue().get(cartRedisKey + user.getId());
            for(CartPojo cart : list){
                if(id == cart.getId()){
                    list.remove(cart);
                    redisTemplate.opsForValue().set(key,list);
                    return EgoResult.ok();
                }
            }
        }

        String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(),tempcartKey);
        Map<Long,CartPojo> map = JsonUtils.jsonToMap(cookieValue,Long.class,CartPojo.class);
        map.remove(id);
        CookieUtils.doSetCookieBase64(ServletUtil.getRequest(),ServletUtil.getResponse(),tempcartKey, JsonUtils.objectToJson(map),25920000); //一个月过期时间
        return EgoResult.ok();
    }

    @Override
    public List<OrderCartPojo> showOrderCart(List<Long> ids) {
        List<OrderCartPojo> listResult = new ArrayList<>();
        TbUser user = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");
        //用户购物车
        List<CartPojo> list  =  (List<CartPojo>)redisTemplate.opsForValue().get(cartRedisKey + user.getId());
        for(long id : ids){
            for(CartPojo cart : list){
                if(cart.getId() == id){
                    OrderCartPojo ocp = new OrderCartPojo();
                    BeanUtils.copyProperties(cart,ocp);
                    //比较库存与购买数量
                    TbItem tbItem = tbItemDubboService.selectById(id);
                    if(tbItem.getNum() < cart.getNum()) ocp.setEnough(false);
                    else ocp.setEnough(true);
                    listResult.add(ocp);
                    break;
                }
            }
        }
        return listResult;
    }

    @Override
    public int deleteUserCart(long userId, long[] ids) {
        try {
            String key = cartRedisKey + userId;
            //用户购物车
            List<CartPojo> list  =  (List<CartPojo>)redisTemplate.opsForValue().get(key);

            for(long id : ids){
                for(CartPojo cart : list){
                    if(id == cart.getId()){
                        list.remove(cart);
                        break;
                    }
                }
            }
            redisTemplate.opsForValue().set(key,list);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
