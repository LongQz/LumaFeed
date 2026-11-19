package com.lumafeed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.lumafeed.mapper")
@SpringBootApplication
public class LumaFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(LumaFeedApplication.class, args);
    }

}
