package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.entity.User;
import com.example.tiyu.entity.UserNotification;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.service.UserNotificationService;
import com.example.tiyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final UserNotificationService notificationService;
    private final UserService userService;

    public NotificationController(UserNotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "我的消息")
    public ApiResponse<List<UserNotification>> list(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        return ApiResponse.success(notificationService.lambdaQuery()
                .eq(UserNotification::getUserId, user.getId())
                .orderByDesc(UserNotification::getCreatedAt)
                .list());
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记消息已读")
    public ApiResponse<UserNotification> read(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        UserNotification notification = notificationService.getById(id);
        if (user == null || notification == null || !notification.getUserId().equals(user.getId())) {
            throw new BusinessException("消息不存在");
        }
        notification.setReadFlag(true);
        notificationService.updateById(notification);
        return ApiResponse.success(notification);
    }
}
