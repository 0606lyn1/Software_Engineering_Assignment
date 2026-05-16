package com.example.tiyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tiyu.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
