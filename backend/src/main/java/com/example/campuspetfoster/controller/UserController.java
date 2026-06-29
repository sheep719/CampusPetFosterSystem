package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.SysUser;
import com.example.campuspetfoster.mapper.SysUserMapper;
import com.example.campuspetfoster.service.PasswordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SysUserMapper sysUserMapper;
    private final PasswordService passwordService;

    public UserController(SysUserMapper sysUserMapper, PasswordService passwordService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordService = passwordService;
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String phone) {
        
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (roleCode != null && !roleCode.isEmpty()) {
            wrapper.eq(SysUser::getRoleCode, roleCode);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(SysUser::getPhone, phone);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        Page<SysUser> userPage = sysUserMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 清除密码字段
        userPage.getRecords().forEach(user -> user.setPassword(null));
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", userPage.getTotal());
        result.put("list", userPage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 清除密码字段
        user.setPassword(null);
        
        return Result.success(user);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 清除密码字段
        user.setPassword(null);
        
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<SysUser> createUser(@RequestBody Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        String roleCode = (String) userData.get("roleCode");
        String phone = (String) userData.get("phone");
        
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (sysUserMapper.selectOne(wrapper) != null) {
            return Result.error("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordService.encodePassword(password));
        user.setRoleCode(roleCode);
        user.setPhone(phone);
        user.setEnabled(1);
        user.setCreateTime(LocalDateTime.now());
        
        sysUserMapper.insert(user);
        
        // 清除密码字段
        user.setPassword(null);
        
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<SysUser> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        SysUser existingUser = sysUserMapper.selectById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        String username = (String) userData.get("username");
        String phone = (String) userData.get("phone");
        String roleCode = (String) userData.get("roleCode");
        Integer enabled = (Integer) userData.get("enabled");
        
        // 如果修改用户名，检查是否已存在
        if (username != null && !username.equals(existingUser.getUsername())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, username);
            if (sysUserMapper.selectOne(wrapper) != null) {
                return Result.error("用户名已存在");
            }
            existingUser.setUsername(username);
        }
        
        if (phone != null) {
            existingUser.setPhone(phone);
        }
        if (roleCode != null) {
            existingUser.setRoleCode(roleCode);
        }
        if (enabled != null) {
            existingUser.setEnabled(enabled);
        }
        
        sysUserMapper.updateById(existingUser);
        
        // 清除密码字段
        existingUser.setPassword(null);
        
        return Result.success(existingUser);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 检查是否为管理员
        if ("admin".equals(user.getRoleCode())) {
            return Result.error("不能删除管理员用户");
        }
        
        sysUserMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    public Result<String> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        String newPassword = passwordData.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        
        user.setPassword(passwordService.encodePassword(newPassword));
        sysUserMapper.updateById(user);
        
        return Result.success("密码重置成功");
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/{id}/toggle-status")
    public Result<SysUser> toggleUserStatus(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 不能禁用管理员
        if ("admin".equals(user.getRoleCode())) {
            return Result.error("不能禁用管理员用户");
        }
        
        int newStatus = user.getEnabled() == 1 ? 0 : 1;
        user.setEnabled(newStatus);
        sysUserMapper.updateById(user);
        
        // 清除密码字段
        user.setPassword(null);
        
        return Result.success(user);
    }

    /**
     * 获取用户统计
     */
    @GetMapping("/stats/summary")
    public Result<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总用户数
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        stats.put("totalUsers", sysUserMapper.selectCount(wrapper));

        // 管理员数量
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, "admin");
        stats.put("adminCount", sysUserMapper.selectCount(wrapper));

        // 寄养者数量
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, "owner");
        stats.put("ownerCount", sysUserMapper.selectCount(wrapper));

        // 被寄养者数量
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, "caregiver");
        stats.put("caregiverCount", sysUserMapper.selectCount(wrapper));

        // 启用用户数
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEnabled, 1);
        stats.put("enabledCount", sysUserMapper.selectCount(wrapper));

        // 禁用用户数
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEnabled, 0);
        stats.put("disabledCount", sysUserMapper.selectCount(wrapper));

        return Result.success(stats);
    }
}