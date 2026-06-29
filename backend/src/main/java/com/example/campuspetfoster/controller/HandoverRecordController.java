package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.HandoverRecord;
import com.example.campuspetfoster.mapper.HandoverRecordMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/handover-records")
public class HandoverRecordController {

    private final HandoverRecordMapper handoverRecordMapper;

    public HandoverRecordController(HandoverRecordMapper handoverRecordMapper) {
        this.handoverRecordMapper = handoverRecordMapper;
    }

    /**
     * 获取交接记录列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getHandoverRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long applicationId,
            @RequestParam(required = false) String handoverType) {
        
        LambdaQueryWrapper<HandoverRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (applicationId != null) {
            wrapper.eq(HandoverRecord::getApplicationId, applicationId);
        }
        if (handoverType != null && !handoverType.isEmpty()) {
            wrapper.eq(HandoverRecord::getHandoverType, handoverType);
        }
        
        wrapper.orderByDesc(HandoverRecord::getHandoverTime);
        
        Page<HandoverRecord> recordPage = handoverRecordMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", recordPage.getTotal());
        result.put("list", recordPage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取指定申请的交接记录
     */
    @GetMapping("/application/{applicationId}")
    public Result<List<HandoverRecord>> getRecordsByApplication(@PathVariable Long applicationId) {
        LambdaQueryWrapper<HandoverRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HandoverRecord::getApplicationId, applicationId);
        wrapper.orderByAsc(HandoverRecord::getHandoverTime);
        
        List<HandoverRecord> records = handoverRecordMapper.selectList(wrapper);
        return Result.success(records);
    }

    /**
     * 获取交接记录详情
     */
    @GetMapping("/{id}")
    public Result<HandoverRecord> getHandoverRecordById(@PathVariable Long id) {
        HandoverRecord record = handoverRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("交接记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 创建交接记录
     */
    @PostMapping
    public Result<HandoverRecord> createHandoverRecord(@RequestBody HandoverRecord record) {
        handoverRecordMapper.insert(record);
        return Result.success(record);
    }

    /**
     * 更新交接记录
     */
    @PutMapping("/{id}")
    public Result<HandoverRecord> updateHandoverRecord(
            @PathVariable Long id,
            @RequestBody HandoverRecord record) {
        
        HandoverRecord existingRecord = handoverRecordMapper.selectById(id);
        if (existingRecord == null) {
            return Result.error("交接记录不存在");
        }
        
        record.setId(id);
        handoverRecordMapper.updateById(record);
        return Result.success(record);
    }

    /**
     * 删除交接记录
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteHandoverRecord(@PathVariable Long id) {
        HandoverRecord record = handoverRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("交接记录不存在");
        }
        
        handoverRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 获取交接记录统计
     */
    @GetMapping("/stats/summary")
    public Result<Map<String, Object>> getHandoverStats() {
        LambdaQueryWrapper<HandoverRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 总交接记录数
        long totalCount = handoverRecordMapper.selectCount(wrapper);
        
        // 交接类型统计
        wrapper.clear();
        wrapper.eq(HandoverRecord::getHandoverType, "delivery");
        long deliveryCount = handoverRecordMapper.selectCount(wrapper);
        
        wrapper.clear();
        wrapper.eq(HandoverRecord::getHandoverType, "return");
        long returnCount = handoverRecordMapper.selectCount(wrapper);
        
        wrapper.clear();
        wrapper.eq(HandoverRecord::getHandoverType, "visit");
        long visitCount = handoverRecordMapper.selectCount(wrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("deliveryCount", deliveryCount);
        stats.put("returnCount", returnCount);
        stats.put("visitCount", visitCount);
        
        return Result.success(stats);
    }
}