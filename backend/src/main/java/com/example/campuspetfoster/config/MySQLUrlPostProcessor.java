package com.example.campuspetfoster.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLUrlPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String mysqlPublicUrl = environment.getProperty("MYSQL_PUBLIC_URL");
        if (mysqlPublicUrl != null && mysqlPublicUrl.startsWith("mysql://")) {
            String jdbcUrl = convertToJdbcUrl(mysqlPublicUrl);
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);
            
            String mysqlUser = environment.getProperty("MYSQL_USER", environment.getProperty("MYSQLUSER", "root"));
            properties.put("spring.datasource.username", mysqlUser);
            
            String mysqlPassword = environment.getProperty("MYSQL_ROOT_PASSWORD", 
                environment.getProperty("MYSQLPASSWORD", ""));
            properties.put("spring.datasource.password", mysqlPassword);
            
            environment.getPropertySources().addFirst(new MapPropertySource("mysql-public-url", properties));
        }
    }

    private String convertToJdbcUrl(String mysqlUrl) {
        Pattern pattern = Pattern.compile("mysql://([^:]+):([^@]+)@([^:]+):(\\d+)/(\\w+)");
        Matcher matcher = pattern.matcher(mysqlUrl);
        if (matcher.matches()) {
            String host = matcher.group(3);
            String port = matcher.group(4);
            String database = matcher.group(5);
            return "jdbc:mysql://" + host + ":" + port + "/" + database 
                + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        }
        return mysqlUrl;
    }
}
