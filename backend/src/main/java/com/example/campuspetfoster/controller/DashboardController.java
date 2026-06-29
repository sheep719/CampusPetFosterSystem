package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.*;
import com.example.campuspetfoster.mapper.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PetMapper petMapper;
    private final FosterApplicationMapper fosterApplicationMapper;
    private final FosterLocationMapper fosterLocationMapper;
    private final SysUserMapper sysUserMapper;
    private final HandoverRecordMapper handoverRecordMapper;
    private final PetCaregiverProfileMapper caregiverProfileMapper;

    public DashboardController(PetMapper petMapper,
                               FosterApplicationMapper fosterApplicationMapper,
                               FosterLocationMapper fosterLocationMapper,
                               SysUserMapper sysUserMapper,
                               HandoverRecordMapper handoverRecordMapper,
                               PetCaregiverProfileMapper caregiverProfileMapper) {
        this.petMapper = petMapper;
        this.fosterApplicationMapper = fosterApplicationMapper;
        this.fosterLocationMapper = fosterLocationMapper;
        this.sysUserMapper = sysUserMapper;
        this.handoverRecordMapper = handoverRecordMapper;
        this.caregiverProfileMapper = caregiverProfileMapper;
    }

    /**
     * 获取首页统计卡片数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 已登记宠物总数
        LambdaQueryWrapper<Pet> petWrapper = new LambdaQueryWrapper<>();
        stats.put("totalPets", petMapper.selectCount(petWrapper));

        // 当前寄养中宠物数
        LambdaQueryWrapper<FosterApplication> inProgressWrapper = new LambdaQueryWrapper<>();
        inProgressWrapper.eq(FosterApplication::getStatus, "in_progress");
        stats.put("fosteringPets", fosterApplicationMapper.selectCount(inProgressWrapper));

        // 本月新增寄养申请数
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<FosterApplication> monthlyWrapper = new LambdaQueryWrapper<>();
        monthlyWrapper.ge(FosterApplication::getCreateTime, startOfMonth);
        stats.put("monthlyApplications", fosterApplicationMapper.selectCount(monthlyWrapper));

        // 待审核申请数
        LambdaQueryWrapper<FosterApplication> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(FosterApplication::getStatus, "pending");
        stats.put("pendingApplications", fosterApplicationMapper.selectCount(pendingWrapper));

        // 今日到期归还提醒数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LambdaQueryWrapper<FosterApplication> todayExpiringWrapper = new LambdaQueryWrapper<>();
        todayExpiringWrapper.eq(FosterApplication::getStatus, "in_progress");
        todayExpiringWrapper.ge(FosterApplication::getExpectedEndTime, todayStart);
        todayExpiringWrapper.lt(FosterApplication::getExpectedEndTime, todayEnd);
        stats.put("todayExpiring", fosterApplicationMapper.selectCount(todayExpiringWrapper));

        // 注册寄养点数量
        LambdaQueryWrapper<FosterLocation> locationWrapper = new LambdaQueryWrapper<>();
        stats.put("totalLocations", fosterLocationMapper.selectCount(locationWrapper));

        // 总用户数
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        stats.put("totalUsers", sysUserMapper.selectCount(userWrapper));

        return Result.success(stats);
    }

    /**
     * 获取申请状态分布
     */
    @GetMapping("/application-status")
    public Result<Map<String, Long>> getApplicationStatusDistribution() {
        Map<String, Long> distribution = new HashMap<>();

        LambdaQueryWrapper<FosterApplication> wrapper;

        // 待审核
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "pending");
        distribution.put("pending", fosterApplicationMapper.selectCount(wrapper));

        // 已通过
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "approved");
        distribution.put("approved", fosterApplicationMapper.selectCount(wrapper));

        // 进行中
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "in_progress");
        distribution.put("in_progress", fosterApplicationMapper.selectCount(wrapper));

        // 已完成
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "completed");
        distribution.put("completed", fosterApplicationMapper.selectCount(wrapper));

        // 已拒绝
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "rejected");
        distribution.put("rejected", fosterApplicationMapper.selectCount(wrapper));

        // 已取消
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "cancelled");
        distribution.put("cancelled", fosterApplicationMapper.selectCount(wrapper));

        return Result.success(distribution);
    }

    /**
     * 获取宠物种类分布
     */
    @GetMapping("/pet-species")
    public Result<Map<String, Long>> getPetSpeciesDistribution() {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Pet::getSpecies);

        List<Pet> pets = petMapper.selectList(wrapper);
        Map<String, Long> distribution = new HashMap<>();

        for (Pet pet : pets) {
            String species = pet.getSpecies();
            if (species != null && !species.isEmpty()) {
                distribution.put(species, distribution.getOrDefault(species, 0L) + 1);
            }
        }

        return Result.success(distribution);
    }

    /**
     * 获取用户角色分布
     */
    @GetMapping("/user-roles")
    public Result<Map<String, Long>> getUserRoleDistribution() {
        Map<String, Long> distribution = new HashMap<>();

        LambdaQueryWrapper<SysUser> wrapper;

        // 管理员
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, "admin");
        distribution.put("admin", sysUserMapper.selectCount(wrapper));

        // 寄养者
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, "owner");
        distribution.put("owner", sysUserMapper.selectCount(wrapper));

        // 被寄养者
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleCode, "caregiver");
        distribution.put("caregiver", sysUserMapper.selectCount(wrapper));

        return Result.success(distribution);
    }

    /**
     * 获取寄养点可用状态分布
     */
    @GetMapping("/location-status")
    public Result<Map<String, Long>> getLocationStatusDistribution() {
        Map<String, Long> distribution = new HashMap<>();

        LambdaQueryWrapper<FosterLocation> wrapper;

        // 可用
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterLocation::getAvailable, 1);
        distribution.put("available", fosterLocationMapper.selectCount(wrapper));

        // 不可用
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterLocation::getAvailable, 0);
        distribution.put("unavailable", fosterLocationMapper.selectCount(wrapper));

        return Result.success(distribution);
    }

    /**
     * 获取近期活动
     */
    @GetMapping("/recent-activities")
    public Result<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();

        // 最近的5条寄养申请
        LambdaQueryWrapper<FosterApplication> applicationWrapper = new LambdaQueryWrapper<>();
        applicationWrapper.orderByDesc(FosterApplication::getCreateTime);
        applicationWrapper.last("LIMIT 5");
        activities.put("recentApplications", fosterApplicationMapper.selectList(applicationWrapper));

        // 最近的5条交接记录
        LambdaQueryWrapper<HandoverRecord> handoverWrapper = new LambdaQueryWrapper<>();
        handoverWrapper.orderByDesc(HandoverRecord::getHandoverTime);
        handoverWrapper.last("LIMIT 5");
        activities.put("recentHandovers", handoverRecordMapper.selectList(handoverWrapper));

        return Result.success(activities);
    }

    /**
     * 获取系统概览信息
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 系统运行时间（简化版）
        overview.put("systemStatus", "running");
        overview.put("version", "1.0.0");

        // 数据库连接状态
        try {
            petMapper.selectCount(new LambdaQueryWrapper<>());
            overview.put("databaseStatus", "connected");
        } catch (Exception e) {
            overview.put("databaseStatus", "disconnected");
        }

        return Result.success(overview);
    }

    /**
     * 获取近7天寄养申请趋势
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getWeeklyTrend() {
        Map<String, Object> trend = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(FosterApplication::getCreateTime, startOfDay);
            wrapper.lt(FosterApplication::getCreateTime, endOfDay);
            counts.add(fosterApplicationMapper.selectCount(wrapper));
        }

        trend.put("dates", dates);
        trend.put("counts", counts);

        return Result.success(trend);
    }

    /**
     * 获取最近活动列表
     */
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivitiesList() {
        List<Map<String, Object>> activities = new ArrayList<>();

        LambdaQueryWrapper<FosterApplication> applicationWrapper = new LambdaQueryWrapper<>();
        applicationWrapper.orderByDesc(FosterApplication::getCreateTime);
        applicationWrapper.last("LIMIT 10");
        List<FosterApplication> recentApplications = fosterApplicationMapper.selectList(applicationWrapper);

        for (FosterApplication app : recentApplications) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", app.getId());
            activity.put("type", "application");
            activity.put("title", "寄养申请");
            activity.put("content", "宠物寄养申请 #" + app.getId());
            activity.put("status", app.getStatus());
            activity.put("time", app.getCreateTime());
            activities.add(activity);
        }

        LambdaQueryWrapper<HandoverRecord> handoverWrapper = new LambdaQueryWrapper<>();
        handoverWrapper.orderByDesc(HandoverRecord::getHandoverTime);
        handoverWrapper.last("LIMIT 10");
        List<HandoverRecord> recentHandovers = handoverRecordMapper.selectList(handoverWrapper);

        for (HandoverRecord record : recentHandovers) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", record.getId());
            activity.put("type", "handover");
            activity.put("title", "交接记录");
            activity.put("content", record.getHandoverType().equals("pickup") ? "宠物领取" : "宠物归还");
            activity.put("status", "completed");
            activity.put("time", record.getHandoverTime());
            activities.add(activity);
        }

        activities.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("time");
            LocalDateTime timeB = (LocalDateTime) b.get("time");
            return timeB.compareTo(timeA);
        });

        if (activities.size() > 10) {
            activities = activities.subList(0, 10);
        }

        return Result.success(activities);
    }

    /**
     * 获取寄养达人榜
     */
    @GetMapping("/top-caregivers")
    public Result<List<Map<String, Object>>> getTopCaregivers() {
        List<Map<String, Object>> topCaregivers = new ArrayList<>();

        LambdaQueryWrapper<PetCaregiverProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PetCaregiverProfile::getTotalCareCount);
        wrapper.last("LIMIT 10");
        List<PetCaregiverProfile> caregivers = caregiverProfileMapper.selectList(wrapper);

        for (PetCaregiverProfile caregiver : caregivers) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", caregiver.getId());
            item.put("name", caregiver.getName());
            item.put("avgRating", caregiver.getAvgRating());
            item.put("totalCareCount", caregiver.getTotalCareCount());
            item.put("distanceKm", caregiver.getDistanceToSchoolKm());
            topCaregivers.add(item);
        }

        return Result.success(topCaregivers);
    }
}