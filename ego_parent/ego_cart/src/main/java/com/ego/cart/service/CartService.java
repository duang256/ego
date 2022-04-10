package com.ego.cart.service;


import com.ego.cart.pojo.OrderCartPojo;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;

import java.util.List;

public interface CartService {
    /**
     * 添加购物车
     * @param id
     * @param num
     */
    void addCart(long id,int num);

    /**
     * 查询购物车
     * @return
     */
     List<CartPojo> showCart();


    /**
     * 修改购物车商品数量
     * @param id
     * @param num
     * @return
     */
    EgoResult updateNum(long id, int num);


    /**
     * 删除购物车商品
     * @param id
     * @return
     */
    EgoResult delete(long id);

}
