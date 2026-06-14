package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.entity.UserNotification;
import com.example.tiyu.mapper.UserNotificationMapper;
import com.example.tiyu.service.UserNotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserNotificationServiceImpl extends ServiceImpl<UserNotificationMapper, UserNotification> implements UserNotificationService {
    @Override
    public void push(Long userId, String type, String title, String content) {
        if (userId == null) {
            return;
        }
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setReadFlag(false);
        notification.setCreatedAt(LocalDateTime.now());
        save(notification);
    }
}
