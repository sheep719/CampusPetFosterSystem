package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.FosterApplication;
import com.example.campuspetfoster.entity.FosterLocation;
import com.example.campuspetfoster.entity.HandoverRecord;
import com.example.campuspetfoster.mapper.FosterApplicationMapper;
import com.example.campuspetfoster.mapper.FosterLocationMapper;
import com.example.campuspetfoster.mapper.HandoverRecordMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foster-applications")
public class FosterApplicationController {

    private final FosterApplicationMapper fosterApplicationMapper;
    private final FosterLocationMapper fosterLocationMapper;
    private final HandoverRecordMapper handoverRecordMapper;

    public FosterApplicationController(FosterApplicationMapper fosterApplicationMapper,
                                       FosterLocationMapper fosterLocationMapper,
                                       HandoverRecordMapper handoverRecordMapper) {
        this.fosterApplicationMapper = fosterApplicationMapper;
        this.fosterLocationMapper = fosterLocationMapper;
        this.handoverRecordMapper = handoverRecordMapper;
    }

    /**
     * 获取寄养申请列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getApplicationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long caregiverId) {
        
        LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(FosterApplication::getStatus, status);
        }
        if (ownerId != null) {
            wrapper.eq(FosterApplication::getOwnerId, ownerId);
        }
        if (caregiverId != null) {
            wrapper.eq(FosterApplication::getCaregiverId, caregiverId);
        }
        
        wrapper.orderByDesc(FosterApplication::getCreateTime);
        
        Page<FosterApplication> applicationPage = fosterApplicationMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", applicationPage.getTotal());
        result.put("list", applicationPage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取我的寄养申请列表
     */
    @GetMapping("/my")
    public Result<List<FosterApplication>> getMyApplications(@RequestHeader("X-User-Id") Long userId) {
        LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getOwnerId, userId);
        wrapper.orderByDesc(FosterApplication::getCreateTime);
        
        List<FosterApplication> applications = fosterApplicationMapper.selectList(wrapper);
        return Result.success(applications);
    }

    /**
     * 获取待我处理的寄养申请（寄养者视角）
     */
    @GetMapping("/pending-caregiver")
    public Result<List<FosterApplication>> getPendingForCaregiver(@RequestHeader("X-User-Id") Long userId) {
        LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getCaregiverId, userId);
        wrapper.eq(FosterApplication::getStatus, "approved");
        wrapper.orderByAsc(FosterApplication::getStartTime);
        
        List<FosterApplication> applications = fosterApplicationMapper.selectList(wrapper);
        return Result.success(applications);
    }

    /**
     * 获取寄养申请详情
     */
    @GetMapping("/{id}")
    public Result<FosterApplication> getApplicationById(@PathVariable Long id) {
        FosterApplication application = fosterApplicationMapper.selectById(id);
        if (application == null) {
            return Result.error("寄养申请不存在");
        }
        return Result.success(application);
    }

    /**
     * 创建寄养申请
     */
    @PostMapping
    @Transactional
    public Result<FosterApplication> createApplication(
            @RequestBody FosterApplication application,
            @RequestHeader("X-User-Id") Long userId) {
        
        // 设置申请人为当前用户
        application.setOwnerId(userId);
        application.setStatus("pending");
        
        // 检查寄养点是否可用
        FosterLocation location = fosterLocationMapper.selectById(application.getLocationId());
        if (location == null) {
            return Result.error("寄养点不存在");
        }
        if (location.getAvailable() == 0) {
            return Result.error("该寄养点当前不可用");
        }
        
        fosterApplicationMapper.insert(application);
        return Result.success(application);
    }

    /**
     * 审核寄养申请（管理员）
     */
    @PutMapping("/{id}/review")
    @Transactional
    public Result<FosterApplication> reviewApplication(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData) {
        
        FosterApplication application = fosterApplicationMapper.selectById(id);
        if (application == null) {
            return Result.error("寄养申请不存在");
        }
        
        if (!"pending".equals(application.getStatus())) {
            return Result.error("该申请已被处理");
        }
        
        Boolean approved = (Boolean) reviewData.get("approved");
        String reason = (String) reviewData.get("reason");
        
        if (approved) {
            application.setStatus("approved");
            application.setCaregiverId(application.getCaregiverId());
        } else {
            application.setStatus("rejected");
            application.setRejectReason(reason);
        }
        
        fosterApplicationMapper.updateById(application);
        return Result.success(application);
    }

    /**
     * 确认寄养开始
     */
    @PutMapping("/{id}/start")
    @Transactional
    public Result<FosterApplication> startFoster(@PathVariable Long id) {
        FosterApplication application = fosterApplicationMapper.selectById(id);
        if (application == null) {
            return Result.error("寄养申请不存在");
        }
        
        if (!"approved".equals(application.getStatus())) {
            return Result.error("该申请尚未通过审核");
        }
        
        // 更新申请状态
        application.setStatus("in_progress");
        application.setActualStartTime(LocalDateTime.now());
        fosterApplicationMapper.updateById(application);
        
        // 创建交接记录
        HandoverRecord handover = new HandoverRecord();
        handover.setApplicationId(id);
        handover.setHandoverType("delivery");
        handover.setHandoverTime(LocalDateTime.now());
        handover.setRemark("寄养开始交接");
        handoverRecordMapper.insert(handover);
        
        return Result.success(application);
    }

    /**
     * 确认寄养结束/归还
     */
    @PutMapping("/{id}/end")
    @Transactional
    public Result<FosterApplication> endFoster(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> endData) {
        
        FosterApplication application = fosterApplicationMapper.selectById(id);
        if (application == null) {
            return Result.error("寄养申请不存在");
        }
        
        if (!"in_progress".equals(application.getStatus())) {
            return Result.error("该申请未在进行中");
        }
        
        // 更新申请状态
        application.setStatus("completed");
        application.setActualEndTime(LocalDateTime.now());
        
        if (endData != null) {
            Integer rating = (Integer) endData.get("rating");
            String reviewText = (String) endData.get("reviewText");
            
            application.setRating(rating);
            application.setReviewText(reviewText);
        }
        
        fosterApplicationMapper.updateById(application);
        
        // 创建归还交接记录
        HandoverRecord handover = new HandoverRecord();
        handover.setApplicationId(id);
        handover.setHandoverType("return");
        handover.setHandoverTime(LocalDateTime.now());
        handover.setRemark("寄养结束归还");
        handoverRecordMapper.insert(handover);
        
        return Result.success(application);
    }

    /**
     * 取消寄养申请
     */
    @PutMapping("/{id}/cancel")
    public Result<FosterApplication> cancelApplication(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> cancelData) {
        
        FosterApplication application = fosterApplicationMapper.selectById(id);
        if (application == null) {
            return Result.error("寄养申请不存在");
        }
        
        if ("in_progress".equals(application.getStatus()) || "completed".equals(application.getStatus())) {
            return Result.error("无法取消已开始或已完成的寄养申请");
        }
        
        application.setStatus("cancelled");
        if (cancelData != null) {
            application.setRejectReason(cancelData.get("reason"));
        }
        
        fosterApplicationMapper.updateById(application);
        return Result.success(application);
    }

    /**
     * 获取即将到期的寄养申请
     */
    @GetMapping("/expiring-soon")
    public Result<List<FosterApplication>> getExpiringSoon(@RequestParam(defaultValue = "3") Integer days) {
        LocalDateTime deadline = LocalDateTime.now().plusDays(days);
        
        LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "in_progress");
        wrapper.le(FosterApplication::getExpectedEndTime, deadline);
        wrapper.orderByAsc(FosterApplication::getExpectedEndTime);
        
        List<FosterApplication> applications = fosterApplicationMapper.selectList(wrapper);
        return Result.success(applications);
    }

    /**
     * 获取已逾期的寄养申请
     */
    @GetMapping("/overdue")
    public Result<List<FosterApplication>> getOverdue() {
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterApplication::getStatus, "in_progress");
        wrapper.lt(FosterApplication::getExpectedEndTime, now);
        wrapper.orderByAsc(FosterApplication::getExpectedEndTime);
        
        List<FosterApplication> applications = fosterApplicationMapper.selectList(wrapper);
        return Result.success(applications);
    }

    /**
     * 获取寄养申请统计
     */
    @GetMapping("/stats/summary")
    public Result<Map<String, Object>> getApplicationStats() {
        LambdaQueryWrapper<FosterApplication> wrapper = new LambdaQueryWrapper<>();
        
        // 总申请数
        long totalCount = fosterApplicationMapper.selectCount(wrapper);
        
        // 待审核数
        wrapper.clear();
        wrapper.eq(FosterApplication::getStatus, "pending");
        long pendingCount = fosterApplicationMapper.selectCount(wrapper);
        
        // 进行中数
        wrapper.clear();
        wrapper.eq(FosterApplication::getStatus, "in_progress");
        long inProgressCount = fosterApplicationMapper.selectCount(wrapper);
        
        // 已完成数
        wrapper.clear();
        wrapper.eq(FosterApplication::getStatus, "completed");
        long completedCount = fosterApplicationMapper.selectCount(wrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("pendingCount", pendingCount);
        stats.put("inProgressCount", inProgressCount);
        stats.put("completedCount", completedCount);
        
        return Result.success(stats);
    }
}