package com.ego.cart.service.impl;

import com.ego.cart.pojo.OrderCartPojo;
import com.ego.cart.service.RedisCartService;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.ServletUtil;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbUser;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisCartServiceImpl implements RedisCartService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${ego.item.redisKey}")
    private String itemRedisKey;


    @Value("${ego.cart.redisKey}")
    private String cartRedisKey;

    @Reference
    private TbItemDubboService tbItemDubboService;


    @Override
    public void addCart(long id, int num) {
        TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        List<CartPojo> list = new ArrayList<>();
        //购物车存储到Redis中
        //设定redis中 key = cart:用户id，同样进行软编码
        String key = cartRedisKey + user.getId();

        //判断是否已经有数据
        if (redisTemplate.hasKey(key)) {
            list = (List<CartPojo>) redisTemplate.opsForValue().get(key);
            //判断当前商品是否存在，如果存在，修改数量
            for (CartPojo cart : list) {
                if (cart.getId() == id) {
                    cart.setNum(cart.getNum() + num);
                    redisTemplate.opsForValue().set(key, list);
                    return;
                }
            }
        }
        //第一次存储redis和购物车无此商品 需要新增 都走这两行代码
        list.add(addItem(id, num));
        redisTemplate.opsForValue().set(key, list);
    }


    public CartPojo addItem(long id, int num) {
        //redis中商品key 软编码
        String key = itemRedisKey + id;
        TbItemDetails tbItemDetails = (TbItemDetails) redisTemplate.opsForValue().get(key);
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
    public List<CartPojo> showCart() {
        //用户购物车
        TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        List<CartPojo> list = (List<CartPojo>) redisTemplate.opsForValue().get(cartRedisKey + user.getId());
        return list;
    }

    @Override
    public EgoResult updateNum(long id, int num) {
        try {
            TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
            String key = cartRedisKey + user.getId();
            //用户购物车
            List<CartPojo> list = (List<CartPojo>) redisTemplate.opsForValue().get(key);
            for (CartPojo cart : list) {
                if (id == cart.getId()) {
                    cart.setNum(num);
                    redisTemplate.opsForValue().set(key, list);
                    return EgoResult.ok();
                }
            }
            return EgoResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EgoResult.err("更新出错");
    }

    @Override
    public EgoResult delete(long id) {
        try {
            TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
            String key = cartRedisKey + user.getId();
            //用户购物车
            List<CartPojo> list = (List<CartPojo>) redisTemplate.opsForValue().get(cartRedisKey + user.getId());
            for (CartPojo cart : list) {
                if (id == cart.getId()) {
                    list.remove(cart);
                    redisTemplate.opsForValue().set(key, list);
                    return EgoResult.ok();
                }
            }
            return EgoResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EgoResult.err("删除出错");
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
