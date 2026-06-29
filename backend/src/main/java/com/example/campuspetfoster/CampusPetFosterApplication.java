package com.example.campuspetfoster;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@SpringBootApplication
@MapperScan("com.example.campuspetfoster.mapper")
@EnableScheduling
public class CampusPetFosterApplication {

    public static void main(String[] args) {
        System.out.println("========== APPLICATION STARTING ==========");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        
        Map<String, String> env = System.getenv();
        System.out.println("Environment Variables:");
        for (String key : env.keySet()) {
            if (key.toUpperCase().contains("MYSQL") || key.toUpperCase().contains("DATABASE")) {
                String value = key.toLowerCase().contains("password") ? "******" : env.get(key);
                System.out.println("  " + key + "=" + value);
            }
        }
        
        System.out.println("==========================================");
        SpringApplication.run(CampusPetFosterApplication.class, args);
    }

    @Bean
    public CommandLineRunner printConfig() {
        return args -> {
            System.out.println("========== APPLICATION STARTED ==========");
            System.out.println("Database URL: " + System.getProperty("spring.datasource.url"));
            System.out.println("Database Username: " + System.getProperty("spring.datasource.username"));
            System.out.println("Database Password: ******");
            System.out.println("==========================================");
        };
    }
}
