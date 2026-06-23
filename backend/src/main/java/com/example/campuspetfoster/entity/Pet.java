package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pet")
public class Pet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private String petName;
    private String species;
    private String breed;
    private String gender;
    private Integer age;
    private String photoUrl;
    private String healthNote;
    private String dietNote;
    private String vaccineStatus;
    private String dewormStatus;
}
