package com.example.campuspetfoster;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.campuspetfoster.mapper")
public class CampusPetFosterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPetFosterApplication.class, args);
    }
}
