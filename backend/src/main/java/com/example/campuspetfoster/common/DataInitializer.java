package com.example.campuspetfoster.common;

import com.example.campuspetfoster.entity.SysUser;
import com.example.campuspetfoster.mapper.SysUserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String encodedPassword = passwordEncoder.encode("123456");

        SysUser admin = sysUserMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (admin != null) {
            admin.setPassword(encodedPassword);
            sysUserMapper.updateById(admin);
        }

        SysUser student = sysUserMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "student001"));
        if (student != null) {
            student.setPassword(encodedPassword);
            sysUserMapper.updateById(student);
        }

        SysUser caregiver = sysUserMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "caregiver001"));
        if (caregiver != null) {
            caregiver.setPassword(encodedPassword);
            sysUserMapper.updateById(caregiver);
        }

        System.out.println("=== 默认密码更新完成 ===");
        System.out.println("admin / 123456");
        System.out.println("student001 / 123456");
        System.out.println("caregiver001 / 123456");
    }
}
