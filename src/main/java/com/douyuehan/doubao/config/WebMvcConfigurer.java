package com.douyuehan.doubao.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {
    /**
     * 拦截器加载
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/avatar/**").addResourceLocations("file:E:/uploadFile/avatar/");
        registry.addResourceHandler("/image/series/**").addResourceLocations("file:E:/uploadFile/series/");
        super.addResourceHandlers(registry);
    }
}