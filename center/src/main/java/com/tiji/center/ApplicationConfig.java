package com.tiji.center;

import com.tiji.center.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 贰拾壹
 * @create 2019-09-05 15:29
 */
@Configuration
public class ApplicationConfig extends WebMvcConfigurationSupport {

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求
        String[] addPathPatterns = {"/**"};
        //不需要拦截的请求
        String[] excludePathPatterns = {"/user/login", "/pluginchecker"};
        registry.addInterceptor(jwtFilter).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }
}
