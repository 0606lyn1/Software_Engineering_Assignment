package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.CommentCreateRequest;
import com.example.tiyu.entity.Comment;
import com.example.tiyu.entity.User;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.service.CommentService;
import com.example.tiyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "评论列表")
    public ApiResponse<List<Comment>> list(@RequestParam(required = false) Long venueId) {
        List<Comment> data = venueId == null
                ? commentService.list()
                : commentService.lambdaQuery().eq(Comment::getVenueId, venueId).list();
        return ApiResponse.success(data);
    }

    @PostMapping
    @Operation(summary = "发布评论")
    public ApiResponse<Comment> create(@Valid @RequestBody CommentCreateRequest request,
                                       Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }

        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setVenueId(request.getVenueId());
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        commentService.save(comment);

        return ApiResponse.success("发布成功", comment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        commentService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }
}
