package com.example.campuspetfoster.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        log.info("数据库连接信息:");
        log.info("URL: {}", url);
        log.info("Username: {}", username);
        
        int maxRetries = 30;
        int retryDelay = 2000;

        for (int i = 1; i <= maxRetries; i++) {
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                log.info("数据库连接成功！");
                return;
            } catch (SQLException e) {
                log.warn("数据库连接失败 (第 {} 次尝试): {}", i, e.getMessage());
                if (i < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("数据库连接检查被中断", ie);
                    }
                }
            }
        }

        throw new RuntimeException("无法连接到数据库，已重试 " + maxRetries + " 次");
    }
}
