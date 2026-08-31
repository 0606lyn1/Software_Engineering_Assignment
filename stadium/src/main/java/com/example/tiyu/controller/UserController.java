package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.UserCreateRequest;
import com.example.tiyu.dto.UserUpdateRequest;
import com.example.tiyu.entity.User;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.security.RoleNames;
import com.example.tiyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "用户列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.list());
    }

    @GetMapping("/staff")
    @Operation(summary = "场地负责人候选列表")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<User>> listStaff() {
        List<User> data = userService.lambdaQuery()
                .in(User::getRole, List.of(RoleNames.STAFF, RoleNames.ADMIN))
                .orderByAsc(User::getUsername)
                .list();
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户")
    public ApiResponse<User> getById(@PathVariable Long id, Authentication authentication) {
        requireAdminOrSelf(id, authentication);
        return ApiResponse.success(userService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增用户")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> create(@Valid @RequestBody UserCreateRequest request) {
        if (userService.lambdaQuery().eq(User::getUsername, request.getUsername()).exists()) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(RoleNames.normalize(request.getRole()));
        user.setEmailReminderEnabled(request.getEmailReminderEnabled() == null || request.getEmailReminderEnabled());
        user.setCreatedAt(LocalDateTime.now());
        userService.save(user);
        return ApiResponse.success("创建成功", user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改用户")
    public ApiResponse<User> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request, Authentication authentication) {
        boolean admin = isAdmin(authentication);
        requireAdminOrSelf(id, authentication);
        User existing = userService.getById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        if (request.getEmailReminderEnabled() != null) {
            existing.setEmailReminderEnabled(request.getEmailReminderEnabled());
        }
        if (admin && request.getRole() != null && !request.getRole().isBlank()) {
            existing.setRole(RoleNames.normalize(request.getRole()));
        }
        userService.updateById(existing);
        return ApiResponse.success("更新成功", existing);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }

    private void requireAdminOrSelf(Long id, Authentication authentication) {
        if (isAdmin(authentication)) {
            return;
        }
        User current = currentUser(authentication);
        if (current == null || !current.getId().equals(id)) {
            throw new BusinessException("无权访问该用户信息");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return userService.findByUsername(authentication.getName());
    }
}
