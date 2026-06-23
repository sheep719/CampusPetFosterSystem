package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pet_owner_profile")
public class PetOwnerProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String studentNo;
    private String name;
    private String school;
    private String major;
    private String dorm;
    private String contactPhone;
    private String photoUrl;
}
