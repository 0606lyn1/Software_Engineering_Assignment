package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.LoginRequest;
import com.example.tiyu.dto.RegisterRequest;
import com.example.tiyu.entity.User;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.security.JwtUtil;
import com.example.tiyu.security.RoleNames;
import com.example.tiyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.lambdaQuery().eq(User::getUsername, request.getUsername()).exists()) {
            throw new BusinessException("用户名已存在");
        }
        if (userService.lambdaQuery().eq(User::getEmail, request.getEmail()).exists()) {
            throw new BusinessException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(RoleNames.STUDENT);
        user.setCreatedAt(LocalDateTime.now());
        userService.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        return ApiResponse.success("注册成功", result);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userService.findByUsername(request.getUsername());
        String role = RoleNames.normalize(user.getRole());
        String token = jwtUtil.generateToken(user.getUsername(), role);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", role,
                "createdAt", user.getCreatedAt()
        ));

        return ApiResponse.success("登录成功", result);
    }
}
