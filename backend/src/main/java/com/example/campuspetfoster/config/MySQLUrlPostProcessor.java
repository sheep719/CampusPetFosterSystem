package com.example.campuspetfoster.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLUrlPostProcessor implements EnvironmentPostProcessor {

    private static final PrintStream out = System.out;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        out.println("========== MySQLUrlPostProcessor DEBUG ==========");
        
        String mysqlPublicUrl = environment.getProperty("MYSQL_PUBLIC_URL");
        out.println("MYSQL_PUBLIC_URL: " + (mysqlPublicUrl == null ? "null" : mysqlPublicUrl));
        
        String mysqlHost = environment.getProperty("MYSQLHOST");
        out.println("MYSQLHOST: " + (mysqlHost == null ? "null" : mysqlHost));
        
        String mysqlPort = environment.getProperty("MYSQLPORT");
        out.println("MYSQLPORT: " + (mysqlPort == null ? "null" : mysqlPort));
        
        String mysqlUser = environment.getProperty("MYSQLUSER");
        out.println("MYSQLUSER: " + (mysqlUser == null ? "null" : mysqlUser));
        
        String mysqlPassword = environment.getProperty("MYSQLPASSWORD");
        out.println("MYSQLPASSWORD: " + (mysqlPassword == null ? "null" : "******"));

        if (mysqlPublicUrl != null && mysqlPublicUrl.startsWith("mysql://")) {
            out.println("Detected MYSQL_PUBLIC_URL, converting to JDBC format...");
            String jdbcUrl = convertToJdbcUrl(mysqlPublicUrl);
            out.println("Converted JDBC URL: " + jdbcUrl);
            
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);
            
            String username = environment.getProperty("MYSQL_USER", environment.getProperty("MYSQLUSER", "root"));
            properties.put("spring.datasource.username", username);
            out.println("Username: " + username);
            
            String password = environment.getProperty("MYSQL_ROOT_PASSWORD", 
                environment.getProperty("MYSQLPASSWORD", ""));
            properties.put("spring.datasource.password", password);
            out.println("Password: ******");
            
            environment.getPropertySources().addFirst(new MapPropertySource("mysql-public-url", properties));
            out.println("Properties added successfully");
        } else {
            out.println("Using application.yml default configuration");
        }
        
        out.println("==================================================");
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
        out.println("Pattern match failed for: " + mysqlUrl);
        return mysqlUrl;
    }
}
