package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.FosterLocation;
import com.example.campuspetfoster.mapper.FosterLocationMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foster-locations")
public class FosterLocationController {

    private final FosterLocationMapper fosterLocationMapper;

    public FosterLocationController(FosterLocationMapper fosterLocationMapper) {
        this.fosterLocationMapper = fosterLocationMapper;
    }

    /**
     * 获取寄养点列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getFosterLocationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) Integer available,
            @RequestParam(required = false) String acceptedSpecies) {
        
        LambdaQueryWrapper<FosterLocation> wrapper = new LambdaQueryWrapper<>();
        
        if (locationName != null && !locationName.isEmpty()) {
            wrapper.like(FosterLocation::getLocationName, locationName);
        }
        if (available != null) {
            wrapper.eq(FosterLocation::getAvailable, available);
        }
        if (acceptedSpecies != null && !acceptedSpecies.isEmpty()) {
            wrapper.like(FosterLocation::getAcceptedSpecies, acceptedSpecies);
        }
        
        wrapper.orderByDesc(FosterLocation::getId);
        
        Page<FosterLocation> locationPage = fosterLocationMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", locationPage.getTotal());
        result.put("list", locationPage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取我的寄养点列表
     */
    @GetMapping("/my")
    public Result<List<FosterLocation>> getMyLocations(@RequestHeader("X-User-Id") Long userId) {
        LambdaQueryWrapper<FosterLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterLocation::getCaregiverId, userId);
        wrapper.orderByDesc(FosterLocation::getId);
        
        List<FosterLocation> locations = fosterLocationMapper.selectList(wrapper);
        return Result.success(locations);
    }

    /**
     * 获取可用的寄养点列表
     */
    @GetMapping("/available")
    public Result<List<FosterLocation>> getAvailableLocations(
            @RequestParam(required = false) String species) {
        
        LambdaQueryWrapper<FosterLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FosterLocation::getAvailable, 1);
        
        if (species != null && !species.isEmpty()) {
            wrapper.like(FosterLocation::getAcceptedSpecies, species);
        }
        
        wrapper.orderByAsc(FosterLocation::getDistanceKm);
        
        List<FosterLocation> locations = fosterLocationMapper.selectList(wrapper);
        return Result.success(locations);
    }

    /**
     * 获取寄养点详情
     */
    @GetMapping("/{id}")
    public Result<FosterLocation> getFosterLocationById(@PathVariable Long id) {
        FosterLocation location = fosterLocationMapper.selectById(id);
        if (location == null) {
            return Result.error("寄养点不存在");
        }
        return Result.success(location);
    }

    /**
     * 创建寄养点
     */
    @PostMapping
    public Result<FosterLocation> createFosterLocation(@RequestBody FosterLocation location, @RequestHeader("X-User-Id") Long userId) {
        location.setCaregiverId(userId);
        location.setAvailable(1);
        fosterLocationMapper.insert(location);
        return Result.success(location);
    }

    /**
     * 更新寄养点信息
     */
    @PutMapping("/{id}")
    public Result<FosterLocation> updateFosterLocation(@PathVariable Long id, @RequestBody FosterLocation location) {
        FosterLocation existingLocation = fosterLocationMapper.selectById(id);
        if (existingLocation == null) {
            return Result.error("寄养点不存在");
        }
        
        location.setId(id);
        fosterLocationMapper.updateById(location);
        return Result.success(location);
    }

    /**
     * 删除寄养点
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteFosterLocation(@PathVariable Long id) {
        FosterLocation location = fosterLocationMapper.selectById(id);
        if (location == null) {
            return Result.error("寄养点不存在");
        }
        
        fosterLocationMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 切换寄养点可用状态
     */
    @PutMapping("/{id}/toggle-status")
    public Result<FosterLocation> toggleLocationStatus(@PathVariable Long id) {
        FosterLocation location = fosterLocationMapper.selectById(id);
        if (location == null) {
            return Result.error("寄养点不存在");
        }
        
        int newStatus = location.getAvailable() == 1 ? 0 : 1;
        location.setAvailable(newStatus);
        fosterLocationMapper.updateById(location);
        
        return Result.success(location);
    }

    /**
     * 获取寄养点统计信息
     */
    @GetMapping("/stats/summary")
    public Result<Map<String, Object>> getLocationStats() {
        LambdaQueryWrapper<FosterLocation> wrapper = new LambdaQueryWrapper<>();
        List<FosterLocation> allLocations = fosterLocationMapper.selectList(wrapper);
        
        int totalCount = allLocations.size();
        int availableCount = (int) allLocations.stream().filter(loc -> loc.getAvailable() == 1).count();
        int unavailableCount = totalCount - availableCount;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("availableCount", availableCount);
        stats.put("unavailableCount", unavailableCount);
        
        return Result.success(stats);
    }
}