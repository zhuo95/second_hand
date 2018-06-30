package com.zz.secondhand.controller.portal;

import com.zz.secondhand.common.ResponseCode;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.service.ICartService;
import com.zz.secondhand.util.CookieUtil;
import com.zz.secondhand.util.JsonUtil;
import com.zz.secondhand.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @GetMapping("buy")
    @ResponseBody
    public ServerResponse getBought(HttpServletRequest request,@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                  @RequestParam(value = "pageSize",defaultValue = "5") int pageSize){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }

        return iCartService.getBought(user.getId(),pageIndex,pageSize);

    }


    @GetMapping("sold")
    @ResponseBody
    public ServerResponse getSold(HttpServletRequest request,@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                  @RequestParam(value = "pageSize",defaultValue = "5") int pageSize){
        //判断session
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }

        return iCartService.getSold(user.getId(),pageIndex,pageSize);

    }

    @DeleteMapping("{id}")
    @ResponseBody
    public ServerResponse cancelOrder(@RequestParam(value = "id") Long id,HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }

        return iCartService.cancelOrder(user.getId(),id);
    }





}
