package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("foster_application")
public class FosterApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long petId;
    private Long locationId;
    private Long caregiverId;
    private Long ownerId;
    private LocalDateTime startTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private String careNote;
    private String emergencyContact;
    private String status;
    private String rejectReason;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createTime;
}
