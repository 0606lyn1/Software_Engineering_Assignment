package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.dto.ReservationCreateRequest;
import com.example.tiyu.dto.ReservationSlotResponse;
import com.example.tiyu.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService extends IService<Reservation> {
    Reservation createReservation(ReservationCreateRequest request, Long currentUserId);
    List<ReservationSlotResponse> listSlots(Long venueId, LocalDate date);
    Reservation cancelReservation(Long id, Long currentUserId, boolean adminOrStaff, String reason);
    Reservation checkIn(String code);
    Reservation appeal(Long id, Long currentUserId, String reason);
    void markExpiredReservations();
}
