package com.douyuehan.doubao.common.access;

import com.alibaba.fastjson.JSON;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.CodeMsg;
import com.douyuehan.doubao.common.security.util.JwtTokenUtil;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.redis.AccessKey;
import com.douyuehan.doubao.redis.RedisService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author imyzt
 * @date 2019/3/21 15:31
 * @description AccessInterceptor
 */
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    private final IUmsUserService userService;
    private final RedisService redisService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;


    @Value("${jwt.tokenHeader:Authorization}")
    private String tokenHeader;
    @Value("${jwt.tokenHead:Bearer }")
    private String tokenHead;

    @Autowired
    public AccessInterceptor(IUmsUserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;

            SysUser miaoshaUser = getUser(request, response);
            UserContext.setUser(miaoshaUser);

            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);

            // 方法没有注解
            if (Objects.isNull(accessLimit)) {
                return true;
            }

            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            int seconds = accessLimit.seconds();
            String key = request.getRequestURI();

            if (needLogin) {
                if (Objects.isNull(miaoshaUser)) {
                    render(response, CodeMsg.SERVER_ERROR);
                    return false;
                }
                key += "_" + miaoshaUser.getId();
            }

            AccessKey accessKey = AccessKey.withExpireSeconds(seconds);
            Integer currentCount = redisService.get(accessKey, key, Integer.class);
            if (null == currentCount) {
                redisService.set(accessKey, key, 1);
            } else if (currentCount < maxCount) {
                redisService.incr(accessKey, key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 向响应流写数据.返回前端错误具体原因
     */
    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        String str = JSON.toJSONString(ApiResult.failed(codeMsg));
        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    private SysUser getUser(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            System.out.println("checking username:{}"+username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                return userService.getUserByUsername(username);
            }
        }
        return null;

    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
