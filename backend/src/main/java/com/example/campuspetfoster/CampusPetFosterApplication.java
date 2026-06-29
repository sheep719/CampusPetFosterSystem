package com.example.campuspetfoster;

import com.example.campuspetfoster.config.DatabasePropertyInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.campuspetfoster.mapper")
@EnableScheduling
public class CampusPetFosterApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CampusPetFosterApplication.class);
        application.addInitializers(new DatabasePropertyInitializer());
        application.run(args);
    }
}
