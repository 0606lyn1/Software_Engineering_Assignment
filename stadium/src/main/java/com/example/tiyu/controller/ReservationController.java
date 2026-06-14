package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.AppealRequest;
import com.example.tiyu.dto.CancelReservationRequest;
import com.example.tiyu.dto.CheckInRequest;
import com.example.tiyu.dto.ReservationCreateRequest;
import com.example.tiyu.dto.ReservationSlotResponse;
import com.example.tiyu.entity.Reservation;
import com.example.tiyu.entity.User;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.service.ReservationService;
import com.example.tiyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','TEACHER','STUDENT','USER')")
    public ApiResponse<List<Reservation>> list(Authentication authentication,
                                               @RequestParam(required = false, defaultValue = "false") boolean all) {
        reservationService.markExpiredReservations();
        if (all && isAdminOrStaff(authentication)) {
            return ApiResponse.success(reservationService.list());
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        return ApiResponse.success(reservationService.lambdaQuery().eq(Reservation::getUserId, user.getId()).list());
    }

    @GetMapping("/slots")
    @Operation(summary = "场馆可预约时段")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT','USER')")
    public ApiResponse<List<ReservationSlotResponse>> slots(@RequestParam Long venueId,
                                                            @RequestParam LocalDate date) {
        return ApiResponse.success(reservationService.listSlots(venueId, date));
    }

    @PostMapping
    @Operation(summary = "创建预约")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT','USER')")
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
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','TEACHER','STUDENT','USER')")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication authentication) {
        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        User user = userService.findByUsername(authentication.getName());
        if (!isAdminOrStaff(authentication) && (user == null || !reservation.getUserId().equals(user.getId()))) {
            throw new BusinessException("无权删除该预约");
        }
        reservationService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消预约")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','TEACHER','STUDENT','USER')")
    public ApiResponse<Reservation> cancel(@PathVariable Long id,
                                           @RequestBody(required = false) CancelReservationRequest request,
                                           Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        Reservation reservation = reservationService.cancelReservation(
                id,
                user == null ? null : user.getId(),
                isAdminOrStaff(authentication),
                request == null ? null : request.getReason()
        );
        return ApiResponse.success("取消成功", reservation);
    }

    @PostMapping("/check-in")
    @Operation(summary = "入场核销")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Reservation> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ApiResponse.success("核销成功", reservationService.checkIn(request.getCode()));
    }

    @PostMapping("/{id}/appeal")
    @Operation(summary = "提交异常申诉")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT','USER')")
    public ApiResponse<Reservation> appeal(@PathVariable Long id,
                                           @Valid @RequestBody AppealRequest request,
                                           Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        return ApiResponse.success("申诉已提交", reservationService.appeal(id, user.getId(), request.getReason()));
    }

    private boolean isAdminOrStaff(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .anyMatch(role -> "ROLE_ADMIN".equals(role) || "ROLE_STAFF".equals(role));
    }
}
