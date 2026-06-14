package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.entity.UserNotification;

public interface UserNotificationService extends IService<UserNotification> {
    void push(Long userId, String type, String title, String content);
}
