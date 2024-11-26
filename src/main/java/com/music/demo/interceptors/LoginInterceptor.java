package com.music.demo.interceptors;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.demo.utils.ApiResponse;
import com.music.demo.utils.JwtUtil;
import com.music.demo.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * 登录拦截器
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
            throws IOException
    {
        // 放行OPTIONS请求
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }
        // 进行令牌验证
        String token = request.getHeader("Authorization");
        try{
            Map<String, Object> claims = JwtUtil.parseToken(token);
            // 使用ThreadLocal工具类存储解析的结果
            ThreadLocalUtil.set(claims);
            // 放行
            return true;
        } catch (JWTDecodeException e){
            log.info("[NOT LOGIN] : BLOCKED");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置 HTTP 状态码 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper()
                    .writeValueAsString(ApiResponse.unauthorized("请登录后进行访问。")));
            return false; // 拦截请求
        } catch (TokenExpiredException e){
            log.info("[TOKEN EXPIRED] : BLOCKED");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置 HTTP 状态码 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper()
                    .writeValueAsString(ApiResponse.unauthorized("登陆过期，请重新登录。")));
            return false;
        }
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex)
            throws Exception
    {
        // 清除ThreadLocal
        ThreadLocalUtil.remove();
    }
}
