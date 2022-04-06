package com.ego.controller;

import com.ego.commons.pojo.EgoResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {
    /**
     * 登录页面
     * @yanyue
     */
    @RequestMapping("/")
    public String login(){
        return "login";
    }

    /**
     * 显示主页面
     * @yanyue
     */
    @RequestMapping("/main")
    public String showIndex(){
        return "index";
    }

    /**
     * 登录成功后处理
     * @yanyue
     */
    @RequestMapping("/loginSuccess")
    @ResponseBody
    public EgoResult loginSuccess(){
        return EgoResult.ok();
    }

    /**
     * restful风格
     * 页面请求页面，拿到该页面 + 视图解析器 返回jsp给页面
     * @return
     */
    @RequestMapping("/{page}")
    public String showPage(@PathVariable String page){
        return page;
    }

    /**
     * 商品编辑
     * @return
     */
    @RequestMapping("/rest/page/item-edit")
    public String showEdit(){
        return "item-edit";
    }




}
