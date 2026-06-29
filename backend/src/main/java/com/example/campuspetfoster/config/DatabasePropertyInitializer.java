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

        log.info("========== 环境变量调试 ==========");
        Map<String, String> sysEnv = System.getenv();
        log.info("  环境变量总数: {}", sysEnv.size());
        for (Map.Entry<String, String> entry : sysEnv.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (key.toLowerCase().contains("password") || key.toLowerCase().contains("pass") || key.toLowerCase().contains("secret")) {
                log.info("  {} = {}", key, "******");
            } else {
                log.info("  {} = {}", key, val.length() > 100 ? val.substring(0, 100) + "..." : val);
            }
        }
        log.info("==================================");

        String mysqlUrl = getEnvFirst("DATABASE_URL", "MYSQL_URL", "MYSQLURL", "");
        String username = getEnvFirst("DATABASE_USERNAME", "MYSQLUSER", "MYSQL_USER", "root");
        String password = getEnvFirst("DATABASE_PASSWORD", "MYSQLPASSWORD", "MYSQL_PASSWORD", "");
        String host = getEnvFirst("MYSQLHOST", "MYSQL_HOST", "DATABASE_HOST", "localhost");
        String port = getEnvFirst("MYSQLPORT", "MYSQL_PORT", "DATABASE_PORT", "3306");
        String database = getEnvFirst("MYSQLDATABASE", "MYSQL_DATABASE", "DATABASE_NAME", "campus_pet_foster");

        mysqlUrl = resolvePlaceholders(mysqlUrl, host, port, database, username, password);

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

    private String getEnvFirst(String... keys) {
        for (String key : keys) {
            String val = System.getenv(key);
            if (val != null && !val.isEmpty()) {
                return val;
            }
        }
        return keys[keys.length - 1];
    }

    private String resolvePlaceholders(String url, String host, String port, String database, String username, String password) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        url = url.replace("${MYSQLHOST}", host)
                .replace("${MYSQL_HOST}", host)
                .replace("${MYSQLPORT}", port)
                .replace("${MYSQL_PORT}", port)
                .replace("${MYSQLDATABASE}", database)
                .replace("${MYSQL_DATABASE}", database)
                .replace("${MYSQLUSER}", username)
                .replace("${MYSQL_USER}", username)
                .replace("${MYSQLPASSWORD}", password)
                .replace("${MYSQL_PASSWORD}", password)
                .replace("${MYSQL_ROOT_PASSWORD}", password);
        return url;
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
