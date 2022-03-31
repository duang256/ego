package com.ego.passport.controller;

import com.ego.commons.pojo.EgoResult;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
public class PassportController {

    @Autowired
    private PassportService passportService;

    /**
     * 显示登录页面
     * @return
     */
    @RequestMapping("/user/showLogin")
    public String showLogin(@RequestHeader(name  ="Referer",required = false) String referer, Model model){
        //referer为访问本接口前的url，登录后如果referer为
        //如果是从注册过来的，不要重新回到注册页面
        if(Strings.isNotEmpty(referer)) {
            if(!referer.endsWith("/user/showRegister")) {
                model.addAttribute("redirect", referer);
            }
        }
        return "login";
    }

    /**
     * 显示注册页面
     * @return
     */
    @RequestMapping("/user/showRegister")
    public String showRegister(){
        return "register";
    }

    // /user/check/1/1

    /**
     * 检查用户是否重复
     * 用户名 phone email  分别为数字1 2 3
     * @param param
     * @param type
     * @return
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public EgoResult check(@PathVariable String param,@PathVariable int type){
        TbUser tbUser  = new TbUser();
        if(type == 1) tbUser.setUsername(param);
        else if(type == 2) tbUser.setPhone(param);
        else if(type == 3) tbUser.setEmail(param);
        return  passportService.check(tbUser);
    }


    /**
     * 注册
     * @param tbUser
     * @return
     */
    @RequestMapping("/user/register")
    @ResponseBody
    public EgoResult register(TbUser tbUser,String pwdRepeat){
        //服务器端校验
        String username = tbUser.getUsername();
        if(username == null || username == "" || !username.matches("^[a-zA-Z0-9]{6,12}$")){
            return EgoResult.err("用户名必须为6-12位数字或字母");
        }

        String email = tbUser.getEmail();
        if(email == null || email == "" || !email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")){
            return EgoResult.err("邮箱格式不正确");
        }

        String password = tbUser.getPassword();
        if(password == null || password == "" || !password.matches("^[a-zA-Z0-9]{6,12}$")){
            return EgoResult.err("密码必须为6-12位数字或字母");
        }
        if(pwdRepeat == null || pwdRepeat == "" || !pwdRepeat.equals(tbUser.getPassword())){
            return EgoResult.err("密码不一致");
        }

        String phone = tbUser.getPhone();
        if(phone == null || phone == "" || !phone.matches("^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$")){
            return EgoResult.err("手机号不存在");
        }
        return passportService.register(tbUser);
    }


    @RequestMapping("/user/login")
    @ResponseBody
    public EgoResult login(TbUser tbUser, HttpSession session){
        //项目中基于高内聚，与用户有关的内容都只在passport模块中
        //如果不做负载均衡，springsession用不上，如果做负载均衡，就需要用springSession
        EgoResult login = passportService.login(tbUser);
        if(login.getStatus() == 200) {
            session.setAttribute("loginUser", login.getData());
            //敏感信息不传回给客户端
            login.setData(null);
        }
        return login;
    }

    @RequestMapping("/user/token/{token}")
    @ResponseBody
    //异步请求需要设置allowCredentials=true表示允许接收cookie数据
    @CrossOrigin(allowCredentials = "true")
    public EgoResult token(HttpSession session){
        Object obj = session.getAttribute("loginUser");
        if(obj != null){
            TbUser user = (TbUser) obj;
            user.setPassword(null);
            return EgoResult.ok(user);
        }
        return EgoResult.err("获取用户信息失败");
    }

    // http://localhost:8084/user/logout/M2E0NDBjNjItZjc5Yi00ODAzLWEzMGUtNDA2YjBlNThhODM1
    @RequestMapping("/user/logout/{token}")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public EgoResult logout(HttpSession session){
        session.invalidate();
        return EgoResult.ok();
    }
}
