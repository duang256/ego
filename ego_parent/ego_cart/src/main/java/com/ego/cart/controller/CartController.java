package com.ego.cart.controller;

import com.ego.cart.service.CartService;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 购物车增加
     * @return
     */
    @RequestMapping("/cart/add/{id}.html")
    public String addCart(@PathVariable long id, int num){
        cartService.addCart(id,num);
        return "cartSuccess";
    }

    @RequestMapping("/cart/cart.html")
    public String showCart(Model model){
        model.addAttribute("cartList",cartService.showCart());
        return "cart";
    }

    /**
     * 修改购物车商品数量
     * @param id 商品id
     * @param num 商品修改数量
     * @return
     */
    @RequestMapping(value={"/cart/update/num/{id}/{num}.action","/service/cart/update/num/{id}/{num}"})
    @ResponseBody
    public EgoResult updateNum(@PathVariable  long id,@PathVariable int num){
        return cartService.updateNum(id,num);
    }



    /**
     * 根据商品id删除购物车商品
     * @param id
     * @return
     */
    @RequestMapping("/cart/delete/{id}.action")
    @ResponseBody
    public EgoResult delete(@PathVariable  long id){
        return cartService.delete(id);
    }


    //   http://localhost:8085/cart/order-cart.html?id=164861432810362&id=1474318759
    @RequestMapping("/cart/order-cart.html")
    public String showOrderCart(@RequestParam("id") List<Long> ids, Model model){
        model.addAttribute("cartList",cartService.showOrderCart(ids));
        return "order-cart";
    }


    @RequestMapping("/cart/deleteByIds")
    @ResponseBody
    public int deleteUserCart(long userId,long[] ids){
        return cartService.deleteUserCart(userId,ids);
    }


}
