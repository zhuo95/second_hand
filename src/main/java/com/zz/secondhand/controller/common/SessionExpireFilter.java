package com.zz.secondhand.controller.common;

import com.zz.secondhand.common.Const;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.util.CookieUtil;
import com.zz.secondhand.util.JsonUtil;
import com.zz.secondhand.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            User user  = JsonUtil.string2Obj(userJsonStr,User.class);
            //如果user不是空则重置session过期时间
            if(user != null){
                RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExTime.REDIS_SESSION_TIME);
            }
        }
        log.info("doFilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
