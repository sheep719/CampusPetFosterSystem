package com.example.campuspetfoster.controller;

import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        Map<String, Object> result = authService.login(username, password);
        if (result != null) {
            return Result.success(result);
        }
        return Result.error(401, "用户名或密码错误");
    }

    @PostMapping("/register/owner")
    public Result<String> registerOwner(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String phone = (String) params.get("phone");
        String studentNo = (String) params.get("studentNo");
        String name = (String) params.get("name");
        String school = (String) params.get("school");
        String major = (String) params.get("major");
        String dorm = (String) params.get("dorm");
        
        boolean success = authService.registerOwner(username, password, phone, studentNo, name, school, major, dorm);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }

    @PostMapping("/register/caregiver")
    public Result<String> registerCaregiver(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String phone = (String) params.get("phone");
        String name = (String) params.get("name");
        String address = (String) params.get("address");
        Double distance = ((Number) params.get("distance")).doubleValue();
        String careTypes = (String) params.get("careTypes");
        
        boolean success = authService.registerCaregiver(username, password, phone, name, address, distance, careTypes);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }
}
