package com.example.campuspetfoster.controller;

import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.service.PasswordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/password/{plainPassword}")
    public Result<String> encodePassword(@PathVariable String plainPassword) {
        String encoded = passwordService.encodePassword(plainPassword);
        return Result.success(encoded);
    }
}
