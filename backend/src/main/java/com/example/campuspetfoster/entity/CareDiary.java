package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("care_diary")
public class CareDiary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long caregiverId;
    private LocalDate diaryDate;
    private String content;
    private String photoUrl;
}
