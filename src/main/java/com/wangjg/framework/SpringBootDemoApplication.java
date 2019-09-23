package com.wangjg.framework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangjg
 * @since 2019/9/23
 */
@SpringBootApplication
@MapperScan(basePackages = "com.wangjg.framework.mapper")
public class SpringBootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}
