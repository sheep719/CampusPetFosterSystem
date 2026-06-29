package com.example.campuspetfoster.service;

import com.example.campuspetfoster.entity.Notification;
import com.example.campuspetfoster.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public void createNotification(Long userId, String type, String title, String content, String linkUrl) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setLinkUrl(linkUrl);
        notification.setReadStatus(false);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    public List<Notification> getRecentNotifications(Long userId, Integer limit) {
        return notificationMapper.selectRecentByUserId(userId, limit);
    }

    public Integer getUnreadCount(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }

    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    public List<Notification> getAllNotifications(Long userId, Integer page, Integer size) {
        return notificationMapper.selectRecentByUserId(userId, size);
    }
}
