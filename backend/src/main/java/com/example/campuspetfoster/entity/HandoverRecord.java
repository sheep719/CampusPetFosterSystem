package com.example.campuspetfoster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("handover_record")
public class HandoverRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private String handoverType;
    private LocalDateTime handoverTime;
    private String fromPerson;
    private String toPerson;
    private String remark;
    private String photoUrl;
}
