package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.ReservationCreateRequest;
import com.example.tiyu.entity.Reservation;
import com.example.tiyu.entity.User;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.service.ReservationService;
import com.example.tiyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "预约列表")
    public ApiResponse<List<Reservation>> list(Authentication authentication,
                                               @RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ApiResponse.success(reservationService.list());
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        return ApiResponse.success(reservationService.lambdaQuery().eq(Reservation::getUserId, user.getId()).list());
    }

    @PostMapping
    @Operation(summary = "创建预约")
    public ApiResponse<Reservation> create(@Valid @RequestBody ReservationCreateRequest request,
                                           Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        Reservation reservation = reservationService.createReservation(request, user.getId());
        return ApiResponse.success("预约成功", reservation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预约")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reservationService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }
}
