package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pet_caregiver_profile")
public class PetCaregiverProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String address;
    private Double distanceToSchoolKm;
    private String phone;
    private Integer experienceYears;
    private Integer hasOwnPets;
    private String careTypes;
    private String bio;
    private String avatarUrl;
    private Double avgRating;
    private Integer totalCareCount;
}
