package com.example.campuspetfoster.common;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("========== APPLICATION STARTED ==========");
        System.out.println("Port: " + System.getenv("PORT"));
        System.out.println("Database: SQLite");
    }
}
