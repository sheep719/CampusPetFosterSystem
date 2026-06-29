package com.example.campuspetfoster.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DatabaseConfig {

    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        String mysqlUrl = getProperty("MYSQL_URL", "MYSQLURL", "");
        String username = getProperty("MYSQLUSER", "MYSQL_USER", "root");
        String password = getProperty("MYSQLPASSWORD", "MYSQL_PASSWORD", "");
        String host = getProperty("MYSQLHOST", "MYSQL_HOST", "localhost");
        String port = getProperty("MYSQLPORT", "MYSQL_PORT", "3306");
        String database = getProperty("MYSQLDATABASE", "MYSQL_DATABASE", "campus_pet_foster");

        String jdbcUrl = buildJdbcUrl(mysqlUrl, host, port, database);

        String[] userPass = extractUserPass(mysqlUrl);
        if (userPass != null) {
            username = userPass[0];
            password = userPass[1];
        }

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

    private String getProperty(String key1, String key2, String defaultValue) {
        String val = env.getProperty(key1);
        if (val != null && !val.isEmpty()) return val;
        val = env.getProperty(key2);
        if (val != null && !val.isEmpty()) return val;
        return defaultValue;
    }

    private String buildJdbcUrl(String mysqlUrl, String host, String port, String database) {
        if (mysqlUrl != null && !mysqlUrl.isEmpty()) {
            if (mysqlUrl.startsWith("jdbc:mysql://")) {
                return appendParams(mysqlUrl);
            }
            if (mysqlUrl.startsWith("mysql://")) {
                String url = mysqlUrl.substring(8);
                int atIndex = url.lastIndexOf('@');
                if (atIndex >= 0) {
                    return appendParams("jdbc:mysql://" + url.substring(atIndex + 1));
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

    private String[] extractUserPass(String mysqlUrl) {
        if (mysqlUrl != null && mysqlUrl.startsWith("mysql://")) {
            String url = mysqlUrl.substring(8);
            int atIndex = url.lastIndexOf('@');
            if (atIndex >= 0) {
                String userPass = url.substring(0, atIndex);
                String[] up = userPass.split(":", 2);
                if (up.length == 2) {
                    return up;
                }
            }
        }
        return null;
    }

    private String appendParams(String url) {
        if (url.contains("?")) {
            return url + "&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        }
        return url + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    }
}
