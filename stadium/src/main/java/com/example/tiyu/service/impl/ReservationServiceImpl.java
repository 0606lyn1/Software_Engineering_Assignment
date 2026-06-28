package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.dto.ReservationCreateRequest;
import com.example.tiyu.dto.ReservationSlotResponse;
import com.example.tiyu.dto.VenueAvailabilityResponse;
import com.example.tiyu.entity.Reservation;
import com.example.tiyu.entity.ReservationRule;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.mapper.ReservationMapper;
import com.example.tiyu.service.ReservationRuleService;
import com.example.tiyu.service.ReservationService;
import com.example.tiyu.service.UserNotificationService;
import com.example.tiyu.service.VenueOpsService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {
    private final VenueOpsService venueOpsService;
    private final ReservationRuleService ruleService;
    private final UserNotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    public ReservationServiceImpl(VenueOpsService venueOpsService,
                                  ReservationRuleService ruleService,
                                  UserNotificationService notificationService) {
        this.venueOpsService = venueOpsService;
        this.ruleService = ruleService;
        this.notificationService = notificationService;
    }

    @Override
    public Reservation createReservation(ReservationCreateRequest request, Long currentUserId) {
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        if (start == null || end == null || !start.isBefore(end)) {
            throw new BusinessException("预约时间不合法，开始时间必须早于结束时间");
        }
        ReservationRule rule = ruleService.getEffectiveRule(request.getVenueId());
        validateRuleWindow(rule, start, end, currentUserId, request.getVenueId());

        VenueAvailabilityResponse availability = venueOpsService.getAvailability(request.getVenueId());
        if (!availability.isAvailable()) {
            throw new BusinessException("场地暂不可预约：" + availability.getReason());
        }

        boolean overlap = lambdaQuery()
                .eq(Reservation::getVenueId, request.getVenueId())
                .notIn(Reservation::getStatus, List.of("CANCELED", "EXPIRED", "NO_SHOW"))
                .lt(Reservation::getStartTime, end)
                .gt(Reservation::getEndTime, start)
                .exists();
        if (overlap) {
            throw new BusinessException("预约冲突：该场地在该时段已被预约");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(currentUserId);
        reservation.setVenueId(request.getVenueId());
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setStatus("BOOKED");
        reservation.setCheckinCode(generateCheckinCode());
        reservation.setCancelDeadline(start.minusHours(rule.getCancelBeforeHours()));
        reservation.setAppealStatus("NONE");
        reservation.setCreatedAt(LocalDateTime.now());
        save(reservation);
        notificationService.push(currentUserId, "RESERVATION", "预约成功",
                "预约已生成，核销码：" + reservation.getCheckinCode());
        return reservation;
    }

    @Override
    public List<ReservationSlotResponse> listSlots(Long venueId, LocalDate date) {
        ReservationRule rule = ruleService.getEffectiveRule(venueId);
        VenueAvailabilityResponse availability = venueOpsService.getAvailability(venueId);
        LocalTime open = LocalTime.parse(rule.getOpenTime());
        LocalTime close = LocalTime.parse(rule.getCloseTime());
        LocalDateTime cursor = LocalDateTime.of(date, open);
        LocalDateTime endOfDay = LocalDateTime.of(date, close);
        List<Reservation> reservations = lambdaQuery()
                .eq(Reservation::getVenueId, venueId)
                .notIn(Reservation::getStatus, List.of("CANCELED", "EXPIRED", "NO_SHOW"))
                .ge(Reservation::getStartTime, cursor.toLocalDate().atStartOfDay())
                .lt(Reservation::getStartTime, cursor.toLocalDate().plusDays(1).atStartOfDay())
                .list();
        List<ReservationSlotResponse> slots = new ArrayList<>();
        while (cursor.plusMinutes(rule.getSlotMinutes()).compareTo(endOfDay) <= 0) {
            LocalDateTime slotStart = cursor;
            LocalDateTime slotEnd = cursor.plusMinutes(rule.getSlotMinutes());
            String status = "AVAILABLE";
            String label = "可预约";
            if (!availability.isAvailable()) {
                status = availability.getStatus();
                label = availability.getReason();
            } else if (slotStart.isBefore(LocalDateTime.now())) {
                status = "CLOSED";
                label = "已过期";
            } else if (isOutsideAdvanceWindow(rule, slotStart)) {
                status = "CLOSED";
                label = "未到预约开放窗口";
            } else if (reservations.stream().anyMatch(item -> item.getStartTime().isBefore(slotEnd) && item.getEndTime().isAfter(slotStart))) {
                status = "BOOKED";
                label = "已预约";
            }
            slots.add(new ReservationSlotResponse(slotStart, slotEnd, status, label));
            cursor = slotEnd;
        }
        return slots;
    }

    @Override
    public Reservation cancelReservation(Long id, Long currentUserId, boolean adminOrStaff, String reason) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        if (!adminOrStaff && !reservation.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权取消该预约");
        }
        if (!adminOrStaff && reservation.getCancelDeadline() != null && LocalDateTime.now().isAfter(reservation.getCancelDeadline())) {
            throw new BusinessException("已超过自助取消时间，请联系管理员或提交异常申诉");
        }
        if (!List.of("BOOKED", "PENDING").contains(reservation.getStatus())) {
            throw new BusinessException("当前状态不可取消");
        }
        reservation.setStatus("CANCELED");
        reservation.setCancelReason(reason);
        updateById(reservation);
        notificationService.push(reservation.getUserId(), "CANCEL", "预约已取消",
                reason == null || reason.isBlank() ? "预约已取消" : reason);
        return reservation;
    }

    @Override
    public Reservation checkIn(String code) {
        Reservation reservation = lambdaQuery().eq(Reservation::getCheckinCode, code).one();
        if (reservation == null) {
            throw new BusinessException("核销码不存在");
        }
        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new BusinessException("该预约当前不可核销");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartTime().minusMinutes(30)) || now.isAfter(reservation.getEndTime().plusMinutes(30))) {
            throw new BusinessException("不在允许核销时间范围内");
        }
        reservation.setStatus("CHECKED_IN");
        reservation.setCheckedInAt(now);
        updateById(reservation);
        notificationService.push(reservation.getUserId(), "CHECK_IN", "入场核销成功", "你的预约已完成入场核销");
        return reservation;
    }

    @Override
    public Reservation appeal(Long id, Long currentUserId, String reason) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        if (!reservation.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权申诉该预约");
        }
        if (!"NO_SHOW".equals(reservation.getStatus())) {
            throw new BusinessException("仅爽约记录可以提交申诉");
        }
        if ("PENDING".equals(reservation.getAppealStatus())) {
            throw new BusinessException("申诉已提交，请等待管理员审核");
        }
        reservation.setAppealReason(reason);
        reservation.setAppealStatus("PENDING");
        updateById(reservation);
        notificationService.push(currentUserId, "APPEAL", "异常申诉已提交", "管理员将审核你的申诉");
        return reservation;
    }

    @Override
    public void markExpiredReservations() {
        List<Reservation> expired = lambdaQuery()
                .eq(Reservation::getStatus, "BOOKED")
                .lt(Reservation::getEndTime, LocalDateTime.now())
                .list();
        for (Reservation reservation : expired) {
            reservation.setStatus("NO_SHOW");
            reservation.setAppealStatus("NONE");
            updateById(reservation);
            notificationService.push(reservation.getUserId(), "VIOLATION", "预约已记为爽约",
                    "预约结束后未完成入场核销，如有特殊情况可提交异常申诉");
        }
    }

    private void validateRuleWindow(ReservationRule rule, LocalDateTime start, LocalDateTime end, Long userId, Long venueId) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new BusinessException("不能预约过去的时间");
        }
        if (isOutsideAdvanceWindow(rule, start)) {
            throw new BusinessException("该场馆仅开放未来 " + rule.getAdvanceDays() + " 天内预约");
        }
        LocalTime open = LocalTime.parse(rule.getOpenTime());
        LocalTime close = LocalTime.parse(rule.getCloseTime());
        if (start.toLocalTime().isBefore(open) || end.toLocalTime().isAfter(close)) {
            throw new BusinessException("预约时间需在开放时段 " + rule.getOpenTime() + " - " + rule.getCloseTime() + " 内");
        }
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes <= 0 || minutes > rule.getMaxHoursPerBooking() * 60L) {
            throw new BusinessException("单次预约最长 " + rule.getMaxHoursPerBooking() + " 小时");
        }
        long dailyCount = lambdaQuery()
                .eq(Reservation::getUserId, userId)
                .notIn(Reservation::getStatus, List.of("CANCELED", "EXPIRED", "NO_SHOW"))
                .ge(Reservation::getStartTime, start.toLocalDate().atStartOfDay())
                .lt(Reservation::getStartTime, start.toLocalDate().plusDays(1).atStartOfDay())
                .count();
        if (dailyCount >= rule.getDailyLimit()) {
            throw new BusinessException("已达到每日预约次数上限");
        }
        WeekFields weekFields = WeekFields.of(Locale.CHINA);
        LocalDate weekStart = start.toLocalDate().with(weekFields.dayOfWeek(), 1);
        long weeklyCount = lambdaQuery()
                .eq(Reservation::getUserId, userId)
                .notIn(Reservation::getStatus, List.of("CANCELED", "EXPIRED", "NO_SHOW"))
                .ge(Reservation::getStartTime, weekStart.atStartOfDay())
                .lt(Reservation::getStartTime, weekStart.plusDays(7).atStartOfDay())
                .count();
        if (weeklyCount >= rule.getWeeklyLimit()) {
            throw new BusinessException("已达到每周预约次数上限");
        }
    }

    private boolean isOutsideAdvanceWindow(ReservationRule rule, LocalDateTime start) {
        return start.toLocalDate().isAfter(LocalDate.now().plusDays(rule.getAdvanceDays()));
    }

    private String generateCheckinCode() {
        for (int i = 0; i < 8; i++) {
            String code = String.valueOf(100000 + secureRandom.nextInt(900000));
            if (!lambdaQuery().eq(Reservation::getCheckinCode, code).exists()) {
                return code;
            }
        }
        return String.valueOf(System.currentTimeMillis()).substring(7);
    }
}
