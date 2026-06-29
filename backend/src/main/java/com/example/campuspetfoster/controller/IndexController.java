package com.example.campuspetfoster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/home")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/register")
    public String register() {
        return "forward:/index.html";
    }

    @GetMapping("/pet")
    public String pet() {
        return "forward:/index.html";
    }

    @GetMapping("/caregiver")
    public String caregiver() {
        return "forward:/index.html";
    }

    @GetMapping("/foster-location")
    public String fosterLocation() {
        return "forward:/index.html";
    }

    @GetMapping("/foster-application")
    public String fosterApplication() {
        return "forward:/index.html";
    }

    @GetMapping("/handover")
    public String handover() {
        return "forward:/index.html";
    }

    @GetMapping("/notification")
    public String notification() {
        return "forward:/index.html";
    }

    @GetMapping("/owner-dashboard")
    public String ownerDashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/owner-pets")
    public String ownerPets() {
        return "forward:/index.html";
    }

    @GetMapping("/owner-applications")
    public String ownerApplications() {
        return "forward:/index.html";
    }

    @GetMapping("/owner-caregivers")
    public String ownerCaregivers() {
        return "forward:/index.html";
    }

    @GetMapping("/owner-handover")
    public String ownerHandover() {
        return "forward:/index.html";
    }

    @GetMapping("/caregiver-dashboard")
    public String caregiverDashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/caregiver-applications")
    public String caregiverApplications() {
        return "forward:/index.html";
    }

    @GetMapping("/caregiver-pets")
    public String caregiverPets() {
        return "forward:/index.html";
    }

    @GetMapping("/caregiver-profile")
    public String caregiverProfile() {
        return "forward:/index.html";
    }

    @GetMapping("/caregiver-handover")
    public String caregiverHandover() {
        return "forward:/index.html";
    }

    @GetMapping("/system/**")
    public String system() {
        return "forward:/index.html";
    }
}
