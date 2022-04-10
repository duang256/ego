package com.ego.cart.controller;

import com.ego.cart.service.CartService;
import com.ego.cart.service.RedisCartService;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.ServletUtil;
import com.ego.pojo.TbUser;
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

    @Autowired
    private RedisCartService redisCartService;


    /**
     * 购物车增加
     *
     * @return
     */
    @RequestMapping("/cart/add/{id}.html")
    public String addCart(@PathVariable long id, int num) {
        TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        try {
            if(user == null){
                cartService.addCart(id, num);
            }else{
                redisCartService.addCart(id,num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "cartSuccess";
    }

    /**
     * 显示购物车
     * 分为登录和未登录两种情况
     * @param model
     * @return
     */
    @RequestMapping("/cart/cart.html")
    public String showCart(Model model) {
        TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        try {
            if(user == null){
                model.addAttribute("cartList", cartService.showCart());
            }else{
                model.addAttribute("cartList", redisCartService.showCart());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "cart";
    }

    /**
     * 修改购物车商品数量
     *
     * @param id  商品id
     * @param num 商品修改数量
     * @return
     */
    @RequestMapping(value = {"/cart/update/num/{id}/{num}.action", "/service/cart/update/num/{id}/{num}"})
    @ResponseBody
    public EgoResult updateNum(@PathVariable long id, @PathVariable int num) {
        TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        if(user == null){
            return cartService.updateNum(id, num);
        }else{
            return redisCartService.updateNum(id, num);
        }
    }


    /**
     * 根据商品id删除购物车商品
     *
     * @param id
     * @return
     */
    @RequestMapping("/cart/delete/{id}.action")
    @ResponseBody
    public EgoResult delete(@PathVariable long id) {
        TbUser user = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        if(user == null){
            return cartService.delete(id);
        }else{
            return  redisCartService.delete(id);
        }
    }


    /**
     * 显示订单页面
     * 此方法有拦截器
     * @param ids
     * @param model
     * @return
     */
    @RequestMapping("/cart/order-cart.html")
    public String showOrderCart(@RequestParam("id") List<Long> ids, Model model) {
        try {
            model.addAttribute("cartList", redisCartService.showOrderCart(ids));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "order-cart";
    }


    /**
     * 下订单时通过rabbitmq异步调用此方法
     * 删除购物车中的对应商品
     * @param userId
     * @param ids
     * @return
     */
    @RequestMapping("/cart/deleteByIds")
    @ResponseBody
    public int deleteUserCart(long userId, long[] ids) {
        try {
            return redisCartService.deleteUserCart(userId, ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
