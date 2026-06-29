package com.example.campuspetfoster.common;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("========== APPLICATION STARTUP ==========");
        System.out.println("Port: " + System.getenv("PORT"));
        System.out.println("Database URL: " + System.getenv("DATABASE_URL"));
        System.out.println("Application is starting...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Application started successfully!");
    }
}
