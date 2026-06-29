package com.example.campuspetfoster.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public String encodePassword(String plainPassword) {
        return plainPassword;
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
