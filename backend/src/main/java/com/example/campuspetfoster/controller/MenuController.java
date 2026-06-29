package com.example.campuspetfoster.controller;

import com.example.campuspetfoster.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    /**
     * 获取菜单列表
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getMenuList() {
        List<Map<String, Object>> menus = getMenuTree();
        return Result.success(menus);
    }

    /**
     * 根据角色获取菜单列表
     */
    @GetMapping("/role/{roleCode}")
    public Result<List<Map<String, Object>>> getMenusByRole(@PathVariable String roleCode) {
        List<Map<String, Object>> menus = getFilteredMenuTree(roleCode);
        return Result.success(menus);
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getMenuById(@PathVariable String id) {
        List<Map<String, Object>> allMenus = getMenuTree();
        
        for (Map<String, Object> menu : allMenus) {
            if (id.equals(menu.get("id"))) {
                return Result.success(menu);
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children = (List<Map<String, Object>>) menu.get("children");
            if (children != null) {
                for (Map<String, Object> child : children) {
                    if (id.equals(child.get("id"))) {
                        return Result.success(child);
                    }
                }
            }
        }
        
        return Result.error("菜单不存在");
    }

    private List<Map<String, Object>> getMenuTree() {
        List<Map<String, Object>> menus = new ArrayList<>();

        // 首页
        Map<String, Object> dashboard = createMenu(
            "dashboard",
            "数据看板",
            "dashboard",
            "/dashboard",
            "Odometer",
            new String[]{"admin", "owner", "caregiver"}
        );
        menus.add(dashboard);

        // 宠物管理（管理员）
        Map<String, Object> pet = createMenu(
            "pet",
            "宠物管理",
            "pet",
            "/pet",
            "Eleme",
            new String[]{"admin"}
        );
        menus.add(pet);

        // 寄养点管理（管理员）
        Map<String, Object> location = createMenu(
            "location",
            "寄养点管理",
            "location",
            "/foster-location",
            "Location",
            new String[]{"admin"}
        );
        menus.add(location);

        // 寄养申请管理（管理员）
        Map<String, Object> application = createMenu(
            "application",
            "寄养申请",
            "application",
            "/foster-application",
            "Document",
            new String[]{"admin"}
        );
        menus.add(application);

        // 交接记录（管理员）
        Map<String, Object> handover = createMenu(
            "handover",
            "交接记录",
            "handover",
            "/handover",
            "Switch",
            new String[]{"admin"}
        );
        menus.add(handover);

        // 我的宠物（寄养者）
        Map<String, Object> ownerPets = createMenu(
            "owner-pets",
            "我的宠物",
            "owner-pets",
            "/owner-pets",
            "Cat",
            new String[]{"owner"}
        );
        menus.add(ownerPets);

        // 我的申请（寄养者）
        Map<String, Object> ownerApps = createMenu(
            "owner-applications",
            "我的申请",
            "owner-applications",
            "/owner-applications",
            "Document",
            new String[]{"owner"}
        );
        menus.add(ownerApps);

        // 我的交接（寄养者）
        Map<String, Object> ownerHandover = createMenu(
            "owner-handover",
            "我的交接",
            "owner-handover",
            "/owner-handover",
            "Switch",
            new String[]{"owner"}
        );
        menus.add(ownerHandover);

        // 寄养者列表（寄养者）
        Map<String, Object> ownerCaregivers = createMenu(
            "owner-caregivers",
            "寄养者列表",
            "owner-caregivers",
            "/owner-caregivers",
            "User",
            new String[]{"owner"}
        );
        menus.add(ownerCaregivers);

        // 我的寄养点（被寄养者）
        Map<String, Object> caregiverLocation = createMenu(
            "caregiver-location",
            "我的寄养点",
            "caregiver-location",
            "/caregiver-dashboard",
            "House",
            new String[]{"caregiver"}
        );
        menus.add(caregiverLocation);

        // 寄养申请（被寄养者）
        Map<String, Object> caregiverApps = createMenu(
            "caregiver-applications",
            "寄养申请",
            "caregiver-applications",
            "/caregiver-applications",
            "Document",
            new String[]{"caregiver"}
        );
        menus.add(caregiverApps);

        // 照料中的宠物（被寄养者）
        Map<String, Object> caregiverPets = createMenu(
            "caregiver-pets",
            "照料中的宠物",
            "caregiver-pets",
            "/caregiver-pets",
            "Cat",
            new String[]{"caregiver"}
        );
        menus.add(caregiverPets);

        // 我的交接（被寄养者）
        Map<String, Object> caregiverHandover = createMenu(
            "caregiver-handover",
            "我的交接",
            "caregiver-handover",
            "/caregiver-handover",
            "Switch",
            new String[]{"caregiver"}
        );
        menus.add(caregiverHandover);

        // 个人资料（被寄养者）
        Map<String, Object> caregiverProfile = createMenu(
            "caregiver-profile",
            "个人资料",
            "caregiver-profile",
            "/caregiver-profile",
            "User",
            new String[]{"caregiver"}
        );
        menus.add(caregiverProfile);

        // 系统管理（管理员）
        Map<String, Object> system = createMenu(
            "system",
            "系统管理",
            "system",
            "/system",
            "Setting",
            new String[]{"admin"}
        );
        
        List<Map<String, Object>> systemChildren = new ArrayList<>();
        systemChildren.add(createMenu(
            "system-user",
            "用户管理",
            "system/user",
            "/system/user",
            "User",
            new String[]{"admin"}
        ));
        systemChildren.add(createMenu(
            "system-role",
            "角色管理",
            "system/role",
            "/system/role",
            "Avatar",
            new String[]{"admin"}
        ));
        systemChildren.add(createMenu(
            "system-menu",
            "菜单管理",
            "system/menu",
            "/system/menu",
            "Menu",
            new String[]{"admin"}
        ));
        systemChildren.add(createMenu(
            "system-dict",
            "字典管理",
            "system/dict",
            "/system/dict",
            "Notebook",
            new String[]{"admin"}
        ));
        system.put("children", systemChildren);
        menus.add(system);

        return menus;
    }

    private List<Map<String, Object>> getFilteredMenuTree(String roleCode) {
        List<Map<String, Object>> allMenus = getMenuTree();
        List<Map<String, Object>> filteredMenus = new ArrayList<>();

        for (Map<String, Object> menu : allMenus) {
            if (hasRoleAccess(menu, roleCode)) {
                filteredMenus.add(menu);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) menu.get("children");
                if (children != null) {
                    List<Map<String, Object>> filteredChildren = new ArrayList<>();
                    for (Map<String, Object> child : children) {
                        if (hasRoleAccess(child, roleCode)) {
                            filteredChildren.add(child);
                        }
                    }
                    menu.put("children", filteredChildren);
                }
            }
        }

        return filteredMenus;
    }

    private boolean hasRoleAccess(Map<String, Object> menu, String roleCode) {
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) menu.get("roles");
        return roles != null && roles.contains(roleCode);
    }

    private Map<String, Object> createMenu(
            String id,
            String name,
            String component,
            String path,
            String icon,
            String[] roles) {
        
        Map<String, Object> menu = new HashMap<>();
        menu.put("id", id);
        menu.put("name", name);
        menu.put("component", component);
        menu.put("path", path);
        menu.put("icon", icon);
        menu.put("roles", roles);
        return menu;
    }
}