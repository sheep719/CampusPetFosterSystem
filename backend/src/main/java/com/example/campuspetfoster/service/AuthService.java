package com.example.campuspetfoster.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campuspetfoster.entity.PetCaregiverProfile;
import com.example.campuspetfoster.entity.PetOwnerProfile;
import com.example.campuspetfoster.entity.SysUser;
import com.example.campuspetfoster.mapper.PetCaregiverProfileMapper;
import com.example.campuspetfoster.mapper.PetOwnerProfileMapper;
import com.example.campuspetfoster.mapper.SysUserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    private final SysUserMapper sysUserMapper;
    private final PetOwnerProfileMapper petOwnerProfileMapper;
    private final PetCaregiverProfileMapper petCaregiverProfileMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecretKey secretKey;

    public AuthService(SysUserMapper sysUserMapper, 
                      PetOwnerProfileMapper petOwnerProfileMapper,
                      PetCaregiverProfileMapper petCaregiverProfileMapper) {
        this.sysUserMapper = sysUserMapper;
        this.petOwnerProfileMapper = petOwnerProfileMapper;
        this.petCaregiverProfileMapper = petCaregiverProfileMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.secretKey = Keys.hmacShaKeyFor("campus-pet-foster-secret-key-2026".getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, Object> login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        
        String token = Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRoleCode())
                .signWith(secretKey)
                .compact();
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRoleCode());
        result.put("userId", user.getId());
        return result;
    }

    @Transactional
    public boolean registerOwner(String username, String password, String phone,
                                String studentNo, String name, String school,
                                String major, String dorm) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (sysUserMapper.selectOne(wrapper) != null) {
            return false;
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoleCode("owner");
        user.setPhone(phone);
        user.setEnabled(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        PetOwnerProfile profile = new PetOwnerProfile();
        profile.setUserId(user.getId());
        profile.setStudentNo(studentNo);
        profile.setName(name);
        profile.setSchool(school);
        profile.setMajor(major);
        profile.setDorm(dorm);
        profile.setContactPhone(phone);
        petOwnerProfileMapper.insert(profile);

        return true;
    }

    @Transactional
    public boolean registerCaregiver(String username, String password, String phone,
                                     String name, String address, Double distance,
                                     String careTypes) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (sysUserMapper.selectOne(wrapper) != null) {
            return false;
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoleCode("caregiver");
        user.setPhone(phone);
        user.setEnabled(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        PetCaregiverProfile profile = new PetCaregiverProfile();
        profile.setUserId(user.getId());
        profile.setName(name);
        profile.setAddress(address);
        profile.setDistanceToSchoolKm(distance);
        profile.setPhone(phone);
        profile.setExperienceYears(0);
        profile.setHasOwnPets(0);
        profile.setCareTypes(careTypes);
        profile.setAvgRating(0.0);
        profile.setTotalCareCount(0);
        petCaregiverProfileMapper.insert(profile);

        return true;
    }
}
