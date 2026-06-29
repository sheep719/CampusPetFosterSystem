package com.example.campuspetfoster.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campuspetfoster.common.Result;
import com.example.campuspetfoster.entity.Pet;
import com.example.campuspetfoster.mapper.PetMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetMapper petMapper;

    public PetController(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    /**
     * 获取宠物列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getPetList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String vaccineStatus) {
        
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        
        if (petName != null && !petName.isEmpty()) {
            wrapper.like(Pet::getPetName, petName);
        }
        if (species != null && !species.isEmpty()) {
            wrapper.eq(Pet::getSpecies, species);
        }
        if (vaccineStatus != null && !vaccineStatus.isEmpty()) {
            wrapper.eq(Pet::getVaccineStatus, vaccineStatus);
        }
        
        wrapper.orderByDesc(Pet::getId);
        
        Page<Pet> petPage = petMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", petPage.getTotal());
        result.put("list", petPage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取我的宠物列表
     */
    @GetMapping("/my")
    public Result<List<Pet>> getMyPets(@RequestHeader("X-User-Id") Long userId) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getOwnerId, userId);
        wrapper.orderByDesc(Pet::getId);
        
        List<Pet> pets = petMapper.selectList(wrapper);
        return Result.success(pets);
    }

    /**
     * 获取宠物详情
     */
    @GetMapping("/{id}")
    public Result<Pet> getPetById(@PathVariable Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) {
            return Result.error("宠物不存在");
        }
        return Result.success(pet);
    }

    /**
     * 创建宠物档案
     */
    @PostMapping
    public Result<Pet> createPet(@RequestBody Pet pet, @RequestHeader("X-User-Id") Long userId) {
        pet.setOwnerId(userId);
        petMapper.insert(pet);
        return Result.success(pet);
    }

    /**
     * 更新宠物档案
     */
    @PutMapping("/{id}")
    public Result<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        Pet existingPet = petMapper.selectById(id);
        if (existingPet == null) {
            return Result.error("宠物不存在");
        }
        
        pet.setId(id);
        petMapper.updateById(pet);
        return Result.success(pet);
    }

    /**
     * 删除宠物档案
     */
    @DeleteMapping("/{id}")
    public Result<String> deletePet(@PathVariable Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) {
            return Result.error("宠物不存在");
        }
        
        petMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 获取宠物种类统计
     */
    @GetMapping("/stats/species")
    public Result<Map<String, Long>> getSpeciesStats() {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Pet::getSpecies);
        
        List<Pet> pets = petMapper.selectList(wrapper);
        Map<String, Long> stats = new HashMap<>();
        
        for (Pet pet : pets) {
            String species = pet.getSpecies();
            if (species != null && !species.isEmpty()) {
                stats.put(species, stats.getOrDefault(species, 0L) + 1);
            }
        }
        
        return Result.success(stats);
    }

    /**
     * 获取健康状态统计
     */
    @GetMapping("/stats/vaccine")
    public Result<Map<String, Long>> getVaccineStats() {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Pet::getVaccineStatus);
        
        List<Pet> pets = petMapper.selectList(wrapper);
        Map<String, Long> stats = new HashMap<>();
        
        for (Pet pet : pets) {
            String status = pet.getVaccineStatus();
            if (status != null && !status.isEmpty()) {
                stats.put(status, stats.getOrDefault(status, 0L) + 1);
            }
        }
        
        return Result.success(stats);
    }
}