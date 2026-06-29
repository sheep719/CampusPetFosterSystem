package com.example.campuspetfoster.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DatabasePropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        String mysqlUrl = getFirst(env, "MYSQL_URL", "MYSQLURL", "DATABASE_URL", "");
        String username = getFirst(env, "MYSQLUSER", "MYSQL_USER", "DATABASE_USERNAME", "root");
        String password = getFirst(env, "MYSQLPASSWORD", "MYSQL_PASSWORD", "DATABASE_PASSWORD", "");
        String host = getFirst(env, "MYSQLHOST", "MYSQL_HOST", "localhost");
        String port = getFirst(env, "MYSQLPORT", "MYSQL_PORT", "3306");
        String database = getFirst(env, "MYSQLDATABASE", "MYSQL_DATABASE", "campus_pet_foster");

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

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbcUrl);
        props.put("spring.datasource.username", username);
        props.put("spring.datasource.password", password);

        env.getPropertySources().addFirst(new MapPropertySource("database-config", props));
    }

    private String getFirst(ConfigurableEnvironment env, String... keys) {
        for (String key : keys) {
            String val = env.getProperty(key);
            if (val != null && !val.isEmpty()) {
                return val;
            }
        }
        return keys[keys.length - 1];
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
