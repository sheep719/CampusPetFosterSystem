package com.example.campuspetfoster.controller;

import com.example.campuspetfoster.entity.Notification;
import com.example.campuspetfoster.service.NotificationService;
import com.example.campuspetfoster.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<List<Notification>> getRecentNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(notificationService.getRecentNotifications(userId, limit));
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(@RequestParam Long userId) {
        Map<String, Integer> result = new HashMap<>();
        result.put("count", notificationService.getUnreadCount(userId));
        return Result.success(result);
    }

    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}
