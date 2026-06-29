DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `role_code` VARCHAR(20) NOT NULL COMMENT '角色编码(owner/caregiver/admin)',
    `phone` VARCHAR(20) COMMENT '手机号',
    `enabled` INT DEFAULT 1 COMMENT '是否启用(1启用/0禁用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `pet_owner_profile`;
CREATE TABLE `pet_owner_profile` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `student_no` VARCHAR(50) COMMENT '学号',
    `name` VARCHAR(50) COMMENT '姓名',
    `school` VARCHAR(100) COMMENT '学校',
    `major` VARCHAR(100) COMMENT '专业',
    `dorm` VARCHAR(100) COMMENT '宿舍地址',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `photo_url` VARCHAR(255) COMMENT '头像URL',
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养者档案表';

DROP TABLE IF EXISTS `pet_caregiver_profile`;
CREATE TABLE `pet_caregiver_profile` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) COMMENT '姓名',
    `address` VARCHAR(200) COMMENT '家庭住址',
    `distance_to_school_km` DOUBLE COMMENT '距离学校(公里)',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `experience_years` INT DEFAULT 0 COMMENT '饲养经验(年)',
    `has_own_pets` INT DEFAULT 0 COMMENT '是否有自家宠物(1是/0否)',
    `care_types` VARCHAR(100) COMMENT '可照顾的宠物类型(JSON)',
    `bio` TEXT COMMENT '个人简介',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `avg_rating` DOUBLE DEFAULT 0.0 COMMENT '平均评分',
    `total_care_count` INT DEFAULT 0 COMMENT '累计照顾次数',
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='被寄养者档案表';

DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '宠物ID',
    `owner_id` BIGINT NOT NULL COMMENT '主人ID',
    `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
    `species` VARCHAR(20) COMMENT '种类(cat/dog/hamster/other)',
    `breed` VARCHAR(50) COMMENT '品种',
    `gender` VARCHAR(10) COMMENT '性别(male/female)',
    `age` INT COMMENT '年龄(岁)',
    `photo_url` VARCHAR(255) COMMENT '照片URL',
    `health_note` TEXT COMMENT '健康备注',
    `diet_note` TEXT COMMENT '饮食备注',
    `vaccine_status` VARCHAR(50) COMMENT '疫苗状态',
    `deworm_status` VARCHAR(50) COMMENT '驱虫状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

DROP TABLE IF EXISTS `foster_location`;
CREATE TABLE `foster_location` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '寄养点ID',
    `caregiver_id` BIGINT NOT NULL COMMENT '被寄养者ID',
    `location_name` VARCHAR(100) NOT NULL COMMENT '寄养点名称',
    `address` VARCHAR(200) COMMENT '详细地址',
    `distance_km` DOUBLE COMMENT '距离学校(公里)',
    `capacity` INT DEFAULT 1 COMMENT '可容纳宠物数量',
    `accepted_species` VARCHAR(100) COMMENT '接受的宠物种类(JSON)',
    `care_period` VARCHAR(50) COMMENT '照顾时间段',
    `cost_desc` VARCHAR(200) COMMENT '费用说明',
    `available` INT DEFAULT 1 COMMENT '是否可接单(1是/0否)',
    `environment_photos` VARCHAR(500) COMMENT '环境照片URL(JSON)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养点表';

DROP TABLE IF EXISTS `foster_application`;
CREATE TABLE `foster_application` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `location_id` BIGINT COMMENT '寄养点ID',
    `caregiver_id` BIGINT NOT NULL COMMENT '被寄养者ID',
    `owner_id` BIGINT NOT NULL COMMENT '寄养者ID',
    `start_time` DATETIME COMMENT '寄养开始时间',
    `expected_end_time` DATETIME COMMENT '预计结束时间',
    `actual_start_time` DATETIME COMMENT '实际开始时间',
    `actual_end_time` DATETIME COMMENT '实际结束时间',
    `care_note` TEXT COMMENT '特殊照料说明',
    `emergency_contact` VARCHAR(100) COMMENT '紧急联系人',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态(pending/accepted/rejected/in_progress/completed/cancelled)',
    `reject_reason` TEXT COMMENT '拒绝原因',
    `rating` INT COMMENT '评分(1-5)',
    `review_text` TEXT COMMENT '评价内容'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养申请表';

DROP TABLE IF EXISTS `care_diary`;
CREATE TABLE `care_diary` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日记ID',
    `application_id` BIGINT NOT NULL COMMENT '寄养申请ID',
    `caregiver_id` BIGINT NOT NULL COMMENT '被寄养者ID',
    `diary_date` DATE NOT NULL COMMENT '日记日期',
    `content` TEXT COMMENT '日记内容',
    `photo_url` VARCHAR(255) COMMENT '照片URL'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照料日记表';

DROP TABLE IF EXISTS `handover_record`;
CREATE TABLE `handover_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交接记录ID',
    `application_id` BIGINT NOT NULL COMMENT '寄养申请ID',
    `handover_type` VARCHAR(20) COMMENT '交接类型(delivery/pickup)',
    `handover_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交接时间',
    `from_person` VARCHAR(50) COMMENT '交接人',
    `to_person` VARCHAR(50) COMMENT '接收人',
    `remark` TEXT COMMENT '备注',
    `photo_url` VARCHAR(255) COMMENT '交接照片URL'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交接记录表';

DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(20) COMMENT '通知类型(expire/rating/system)',
    `title` VARCHAR(100) COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `link_url` VARCHAR(255) COMMENT '跳转链接',
    `read_status` BOOLEAN DEFAULT false COMMENT '是否已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `read_time` DATETIME COMMENT '阅读时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

INSERT INTO `sys_user` (`username`, `password`, `role_code`, `phone`, `enabled`, `create_time`) VALUES
('admin', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'admin', '13800138000', 1, NOW()),
('student001', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'owner', '13900139001', 1, NOW()),
('caregiver001', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'caregiver', '13900139002', 1, NOW());

INSERT INTO `pet_owner_profile` (`user_id`, `student_no`, `name`, `school`, `major`, `dorm`, `contact_phone`) VALUES
(2, '2024001', '张三', 'XX大学', '计算机科学与技术', '男生宿舍1号楼301', '13900139001');

INSERT INTO `pet_caregiver_profile` (`user_id`, `name`, `address`, `distance_to_school_km`, `phone`, `experience_years`, `has_own_pets`, `care_types`, `bio`, `avg_rating`, `total_care_count`) VALUES
(3, '李阿姨', 'XX小区3号楼201室', 1.5, '13900139002', 5, 1, '["cat","dog"]', '喜欢小动物，有多年养猫经验', 4.8, 12);

INSERT INTO `pet` (`owner_id`, `pet_name`, `species`, `breed`, `gender`, `age`, `health_note`, `diet_note`, `vaccine_status`, `deworm_status`) VALUES
(2, '咪咪', 'cat', '英短', 'female', 2, '身体健康，性格温顺', '每天喂食两次，喜欢吃鱼罐头', '已完成疫苗接种', '已驱虫');

INSERT INTO `foster_location` (`caregiver_id`, `location_name`, `address`, `distance_km`, `capacity`, `accepted_species`, `care_period`, `cost_desc`, `available`) VALUES
(3, '李阿姨家', 'XX小区3号楼201室', 1.5, 3, '["cat","dog"]', '全天', '免费', 1);
