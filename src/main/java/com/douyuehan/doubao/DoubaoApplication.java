package com.douyuehan.doubao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@MapperScan("com.douyuehan.doubao.mapper")
@SpringBootApplication
public class DoubaoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DoubaoApplication.class, args);
    }
}

