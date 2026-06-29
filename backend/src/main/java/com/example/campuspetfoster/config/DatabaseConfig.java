package com.example.campuspetfoster.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Value("${MYSQL_URL:${MYSQLURL:}}")
    private String mysqlUrl;

    @Value("${MYSQLUSER:${MYSQL_USER:root}}")
    private String username;

    @Value("${MYSQLPASSWORD:${MYSQL_PASSWORD:}}")
    private String password;

    @Value("${MYSQLHOST:${MYSQL_HOST:localhost}}")
    private String host;

    @Value("${MYSQLPORT:${MYSQL_PORT:3306}}")
    private String port;

    @Value("${MYSQLDATABASE:${MYSQL_DATABASE:campus_pet_foster}}")
    private String database;

    @Bean
    public DataSource dataSource() {
        String jdbcUrl = buildJdbcUrl();

        log.info("========== 数据库配置 ==========");
        log.info("JDBC URL: {}", jdbcUrl);
        log.info("Username: {}", username);
        log.info("Password: {}", (password != null && !password.isEmpty()) ? "******" : "空");
        log.info("================================");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private String buildJdbcUrl() {
        if (mysqlUrl != null && !mysqlUrl.isEmpty()) {
            if (mysqlUrl.startsWith("jdbc:mysql://")) {
                return appendParams(mysqlUrl);
            }
            if (mysqlUrl.startsWith("mysql://")) {
                String url = mysqlUrl.substring(8);
                int atIndex = url.lastIndexOf('@');
                if (atIndex >= 0) {
                    String userPass = url.substring(0, atIndex);
                    String hostDb = url.substring(atIndex + 1);
                    String[] up = userPass.split(":", 2);
                    if (up.length == 2) {
                        username = up[0];
                        password = up[1];
                    }
                    return appendParams("jdbc:mysql://" + hostDb);
                }
                return appendParams("jdbc:mysql://" + url);
            }
            return mysqlUrl;
        }

        return String.format(
                "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true",
                host, port, database
        );
    }

    private String appendParams(String url) {
        if (url.contains("?")) {
            return url + "&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        }
        return url + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    }
}
