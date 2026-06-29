package com.example.campuspetfoster.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${MYSQL_PUBLIC_URL:}")
    private String mysqlPublicUrl;

    @PostConstruct
    public void init() {
        if (mysqlPublicUrl != null && !mysqlPublicUrl.isEmpty()) {
            String jdbcFormatUrl = convertToJdbcUrl(mysqlPublicUrl);
            System.setProperty("spring.datasource.url", jdbcFormatUrl);
        }
    }

    private String convertToJdbcUrl(String mysqlUrl) {
        Pattern pattern = Pattern.compile("mysql://([^:]+):([^@]+)@([^:]+):(\\d+)/(\\w+)");
        Matcher matcher = pattern.matcher(mysqlUrl);
        if (matcher.matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            String host = matcher.group(3);
            String port = matcher.group(4);
            String database = matcher.group(5);
            System.setProperty("spring.datasource.username", username);
            System.setProperty("spring.datasource.password", password);
            return "jdbc:mysql://" + host + ":" + port + "/" + database 
                + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        }
        return mysqlUrl;
    }
}
