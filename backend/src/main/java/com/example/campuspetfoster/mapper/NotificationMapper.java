package com.example.campuspetfoster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campuspetfoster.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<Notification> selectRecentByUserId(Long userId, Integer limit);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND read_status = false")
    Integer countUnreadByUserId(Long userId);

    @Update("UPDATE notification SET read_status = true, read_time = NOW() WHERE id = #{id}")
    void markAsRead(Long id);

    @Update("UPDATE notification SET read_status = true, read_time = NOW() WHERE user_id = #{userId}")
    void markAllAsRead(Long userId);
}
