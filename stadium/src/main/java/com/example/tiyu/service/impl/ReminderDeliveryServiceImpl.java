package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.entity.ReminderDelivery;
import com.example.tiyu.entity.Reservation;
import com.example.tiyu.entity.User;
import com.example.tiyu.entity.Venue;
import com.example.tiyu.mapper.ReminderDeliveryMapper;
import com.example.tiyu.service.ReminderDeliveryService;
import com.example.tiyu.service.ReservationService;
import com.example.tiyu.service.UserNotificationService;
import com.example.tiyu.service.UserService;
import com.example.tiyu.service.VenueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReminderDeliveryServiceImpl extends ServiceImpl<ReminderDeliveryMapper, ReminderDelivery> implements ReminderDeliveryService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final List<ReminderWindow> WINDOWS = List.of(
            new ReminderWindow("24H", 24, "24小时提醒已发送"),
            new ReminderWindow("2H", 2, "2小时提醒已发送")
    );

    private final ReservationService reservationService;
    private final UserService userService;
    private final VenueService venueService;
    private final UserNotificationService notificationService;
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final String appPublicUrl;
    private final String mailHost;

    public ReminderDeliveryServiceImpl(@Lazy ReservationService reservationService,
                                       UserService userService,
                                       VenueService venueService,
                                       UserNotificationService notificationService,
                                       JavaMailSender mailSender,
                                       @Value("${app.mail.from:}") String mailFrom,
                                       @Value("${app.public-url:http://localhost:5173}") String appPublicUrl,
                                       @Value("${spring.mail.host:}") String mailHost) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.venueService = venueService;
        this.notificationService = notificationService;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.appPublicUrl = appPublicUrl;
        this.mailHost = mailHost;
    }

    @Override
    @Scheduled(fixedDelayString = "${app.reminders.scan-delay-ms:300000}", initialDelayString = "${app.reminders.initial-delay-ms:30000}")
    public void processUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxStart = now.plusHours(24).plusMinutes(5);
        List<Reservation> reservations = reservationService.lambdaQuery()
                .eq(Reservation::getStatus, "BOOKED")
                .ge(Reservation::getStartTime, now)
                .le(Reservation::getStartTime, maxStart)
                .list();
        for (Reservation reservation : reservations) {
            for (ReminderWindow window : WINDOWS) {
                LocalDateTime threshold = reservation.getStartTime().minusHours(window.hoursBefore());
                if (!now.isBefore(threshold)) {
                    sendReminderIfNeeded(reservation, window);
                }
            }
        }
    }

    @Override
    public void attachStatuses(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return;
        }
        List<Long> ids = reservations.stream().map(Reservation::getId).filter(Objects::nonNull).toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, List<ReminderDelivery>> byReservation = lambdaQuery()
                .in(ReminderDelivery::getReservationId, ids)
                .list()
                .stream()
                .collect(Collectors.groupingBy(ReminderDelivery::getReservationId));
        for (Reservation reservation : reservations) {
            List<String> statuses = new ArrayList<>();
            for (ReminderDelivery delivery : byReservation.getOrDefault(reservation.getId(), List.of())) {
                if (!"EMAIL".equals(delivery.getChannel())) {
                    continue;
                }
                ReminderWindow window = windowByType(delivery.getReminderType());
                String label = window == null ? delivery.getReminderType() : window.successLabel();
                if ("SENT".equals(delivery.getStatus())) {
                    statuses.add(label);
                } else if ("FAILED".equals(delivery.getStatus())) {
                    statuses.add(label.replace("已发送", "邮件失败"));
                }
            }
            reservation.setReminderStatuses(statuses);
        }
    }

    private void sendReminderIfNeeded(Reservation reservation, ReminderWindow window) {
        if (lambdaQuery()
                .eq(ReminderDelivery::getReservationId, reservation.getId())
                .eq(ReminderDelivery::getReminderType, window.type())
                .exists()) {
            return;
        }
        User user = userService.getById(reservation.getUserId());
        Venue venue = venueService.getById(reservation.getVenueId());
        String title = "体育场预约提醒：" + (venue == null ? "场馆" + reservation.getVenueId() : venue.getName())
                + " " + reservation.getStartTime().format(FORMATTER);
        String content = buildContent(reservation, venue);
        notificationService.push(reservation.getUserId(), "REMINDER", title, content);

        ReminderDelivery delivery = new ReminderDelivery();
        delivery.setReservationId(reservation.getId());
        delivery.setUserId(reservation.getUserId());
        delivery.setReminderType(window.type());
        delivery.setScheduledAt(reservation.getStartTime().minusHours(window.hoursBefore()));
        delivery.setAttemptCount(1);
        delivery.setCreatedAt(LocalDateTime.now());

        if (user != null && Boolean.FALSE.equals(user.getEmailReminderEnabled())) {
            delivery.setChannel("IN_APP");
            delivery.setStatus("SENT");
            delivery.setSentAt(LocalDateTime.now());
            save(delivery);
            return;
        }

        delivery.setChannel("EMAIL");
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            markFailed(delivery, "用户邮箱为空");
            return;
        }
        if (mailHost == null || mailHost.isBlank() || mailFrom == null || mailFrom.isBlank()) {
            markFailed(delivery, "邮件服务未配置");
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(user.getEmail());
            message.setSubject(title);
            message.setText(content);
            mailSender.send(message);
            delivery.setStatus("SENT");
            delivery.setSentAt(LocalDateTime.now());
            save(delivery);
        } catch (Exception ex) {
            markFailed(delivery, ex.getMessage());
        }
    }

    private String buildContent(Reservation reservation, Venue venue) {
        String venueName = venue == null ? "场馆" + reservation.getVenueId() : venue.getName();
        return "你的预约即将开始，请按时到场。\n\n"
                + "场馆：" + venueName + "\n"
                + "时间：" + reservation.getStartTime().format(FORMATTER) + " - " + reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
                + "核销码：" + reservation.getCheckinCode() + "\n"
                + "取消截止：" + (reservation.getCancelDeadline() == null ? "无" : reservation.getCancelDeadline().format(FORMATTER)) + "\n"
                + "系统入口：" + appPublicUrl + "\n\n"
                + "如无法到场，请尽早取消预约，避免被记为爽约。";
    }

    private void markFailed(ReminderDelivery delivery, String error) {
        delivery.setStatus("FAILED");
        delivery.setErrorMessage(error == null ? "未知错误" : error.substring(0, Math.min(error.length(), 500)));
        save(delivery);
    }

    private ReminderWindow windowByType(String type) {
        return WINDOWS.stream().filter(item -> item.type().equals(type)).findFirst().orElse(null);
    }

    private record ReminderWindow(String type, int hoursBefore, String successLabel) {
    }
}
