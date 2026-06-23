package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("foster_location")
public class FosterLocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long caregiverId;
    private String locationName;
    private String address;
    private Double distanceKm;
    private Integer capacity;
    private String acceptedSpecies;
    private String carePeriod;
    private String costDesc;
    private Integer available;
    private String environmentPhotos;
}
