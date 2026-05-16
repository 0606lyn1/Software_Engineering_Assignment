package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.dto.ReservationCreateRequest;
import com.example.tiyu.entity.Reservation;

public interface ReservationService extends IService<Reservation> {
    Reservation createReservation(ReservationCreateRequest request, Long currentUserId);
}
