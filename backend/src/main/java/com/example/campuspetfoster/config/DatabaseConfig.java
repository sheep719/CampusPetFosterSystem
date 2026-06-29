package com.example.campuspetfoster.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void init() {
        log.info("========== 数据库配置信息 ==========");
        log.info("URL: {}", url);
        log.info("Username: {}", username);
        log.info("Password: {}", password != null && !password.isEmpty() ? "******" : "空");
        log.info("====================================");
    }
}
