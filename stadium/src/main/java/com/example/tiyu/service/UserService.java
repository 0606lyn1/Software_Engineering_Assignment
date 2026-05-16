package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}
