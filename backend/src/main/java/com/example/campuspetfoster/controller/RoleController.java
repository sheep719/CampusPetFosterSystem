package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.SysUser;
import com.example.campuspetfoster.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final SysUserMapper sysUserMapper;

    public RoleController(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 获取角色列表
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getRoleList() {
        List<Map<String, Object>> roles = new ArrayList<>();

        // 管理员角色
        Map<String, Object> admin = new HashMap<>();
        admin.put("roleCode", "admin");
        admin.put("roleName", "系统管理员");
        admin.put("description", "拥有系统所有权限，负责审核和管理");
        admin.put("enabled", true);
        roles.add(admin);

        // 寄养者角色
        Map<String, Object> owner = new HashMap<>();
        owner.put("roleCode", "owner");
        owner.put("roleName", "寄养者");
        owner.put("description", "在校学生，需要寄养宠物");
        owner.put("enabled", true);
        roles.add(owner);

        // 被寄养者角色
        Map<String, Object> caregiver = new HashMap<>();
        caregiver.put("roleCode", "caregiver");
        caregiver.put("roleName", "被寄养者");
        caregiver.put("description", "学校附近居民，愿意照顾宠物");
        caregiver.put("enabled", true);
        roles.add(caregiver);

        return Result.success(roles);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{roleCode}")
    public Result<Map<String, Object>> getRoleByCode(@PathVariable String roleCode) {
        Map<String, Object> role = new HashMap<>();

        switch (roleCode) {
            case "admin":
                role.put("roleCode", "admin");
                role.put("roleName", "系统管理员");
                role.put("description", "拥有系统所有权限，负责审核和管理");
                role.put("permissions", getAdminPermissions());
                break;
            case "owner":
                role.put("roleCode", "owner");
                role.put("roleName", "寄养者");
                role.put("description", "在校学生，需要寄养宠物");
                role.put("permissions", getOwnerPermissions());
                break;
            case "caregiver":
                role.put("roleCode", "caregiver");
                role.put("roleName", "被寄养者");
                role.put("description", "学校附近居民，愿意照顾宠物");
                role.put("permissions", getCaregiverPermissions());
                break;
            default:
                return Result.error("角色不存在");
        }

        return Result.success(role);
    }

    /**
     * 获取角色用户数
     */
    @GetMapping("/{roleCode}/users-count")
    public Result<Map<String, Object>> getRoleUsersCount(@PathVariable String roleCode) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, roleCode);
        long count = sysUserMapper.selectCount(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("roleCode", roleCode);
        result.put("userCount", count);

        return Result.success(result);
    }

    private List<String> getAdminPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("system:user:read");
        permissions.add("system:user:create");
        permissions.add("system:user:update");
        permissions.add("system:user:delete");
        permissions.add("system:role:read");
        permissions.add("system:menu:read");
        permissions.add("system:dict:read");
        permissions.add("pet:read");
        permissions.add("pet:create");
        permissions.add("pet:update");
        permissions.add("pet:delete");
        permissions.add("location:read");
        permissions.add("location:create");
        permissions.add("location:update");
        permissions.add("location:delete");
        permissions.add("application:read");
        permissions.add("application:review");
        permissions.add("handover:read");
        permissions.add("handover:create");
        permissions.add("handover:update");
        permissions.add("handover:delete");
        permissions.add("notification:read");
        permissions.add("dashboard:read");
        return permissions;
    }

    private List<String> getOwnerPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("pet:read");
        permissions.add("pet:create");
        permissions.add("pet:update");
        permissions.add("pet:delete");
        permissions.add("location:read");
        permissions.add("application:read");
        permissions.add("application:create");
        permissions.add("application:update");
        permissions.add("application:cancel");
        permissions.add("handover:read");
        permissions.add("notification:read");
        permissions.add("dashboard:read");
        return permissions;
    }

    private List<String> getCaregiverPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("location:read");
        permissions.add("location:create");
        permissions.add("location:update");
        permissions.add("location:delete");
        permissions.add("application:read");
        permissions.add("application:process");
        permissions.add("handover:read");
        permissions.add("handover:create");
        permissions.add("handover:update");
        permissions.add("notification:read");
        permissions.add("dashboard:read");
        return permissions;
    }
}