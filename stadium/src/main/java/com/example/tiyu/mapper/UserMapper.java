package com.example.tiyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tiyu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
