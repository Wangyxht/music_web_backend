package com.music.demo.config;

import com.music.demo.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 以下接口无需拦截
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/login",
                        "/user/sms-login",
                        "/user/register",
                        "/user/retrieve",
                        "/song/play/**",
                        "/song/cover/**",
                        "/playlist/cover/**",
                        "/sms/**",
                        "/playlist/recommend"
                )
                .excludePathPatterns(
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/v3/**",
                        "/error"
                );
    }
}
