package com.tiji.center;

import com.tiji.center.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

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
        registry.addInterceptor(jwtFilter)
                .addPathPatterns("/**")
                //login不需要经过filter
                .excludePathPatterns("/**/login")
                //插件HTTP辅助验证不需要经过filter
                .excludePathPatterns("/**/pluginchecker");
        //.excludePathPatterns("/**/logout")
        //.excludePathPatterns("/**/info");
    }
}
