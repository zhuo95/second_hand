package com.zz.secondhand.controller.portal;

import com.zz.secondhand.common.Const;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.service.IUserService;
import com.zz.secondhand.util.CookieUtil;
import com.zz.secondhand.util.JsonUtil;
import com.zz.secondhand.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @GetMapping
    @ResponseBody
    public String test(){
        return "test";
    }

    @PostMapping("/login")
    @ResponseBody
    public ServerResponse login(String username , String password, HttpSession session, HttpServletResponse rs,HttpServletRequest request){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
           // session.setAttribute(Const.CURRENT_USER,response.getData());
            //放入redis
            RedisShardedPoolUtil.setEx(session.getId(), Const.RedisCacheExTime.REDIS_SESSION_TIME ,JsonUtil.obj2String(response.getData()));
            //放入cookie
            CookieUtil.writeLoginToken(rs,session.getId());
        }
        return response;
    }

    @GetMapping("/info")
    @ResponseBody
    public ServerResponse getUserInfo(HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user!=null){
            return ServerResponse.creatBySuccess(user);
        }
        return ServerResponse.creatByErrorMessage("用户未登录");
    }

    //logout
    @GetMapping("/logout")
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request,HttpServletResponse response){
        String loginToken = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        RedisShardedPoolUtil.del(loginToken);
        return ServerResponse.creatBySuccess();
    }

    @PostMapping("/register")
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }


    @GetMapping({"id"})
    public ServerResponse<User> getUserInfoById(@RequestParam(value = "id") Long id){
        return iUserService.getUserInfoById(id);
    }

}
