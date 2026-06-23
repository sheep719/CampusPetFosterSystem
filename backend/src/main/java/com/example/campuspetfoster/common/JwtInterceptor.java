package com.example.campuspetfoster.common;

import com.example.campuspetfoster.entity.SysUser;
import com.example.campuspetfoster.mapper.SysUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final SysUserMapper sysUserMapper;
    private final SecretKey secretKey;

    public JwtInterceptor(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
        this.secretKey = Keys.hmacShaKeyFor("campus-pet-foster-secret-key-2026".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                
                Long userId = claims.get("userId", Long.class);
                String role = claims.get("role", String.class);
                
                if (userId != null) {
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", role);
                    return true;
                }
            } catch (Exception e) {
                response.setStatus(401);
                response.getWriter().write("Invalid token");
                return false;
            }
        }
        response.setStatus(401);
        response.getWriter().write("Unauthorized");
        return false;
    }
}
