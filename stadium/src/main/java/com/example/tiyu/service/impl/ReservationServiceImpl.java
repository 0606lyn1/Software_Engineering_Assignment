package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.dto.ReservationCreateRequest;
import com.example.tiyu.entity.Reservation;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.mapper.ReservationMapper;
import com.example.tiyu.service.ReservationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {
    @Override
    public Reservation createReservation(ReservationCreateRequest request, Long currentUserId) {
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        if (start == null || end == null || !start.isBefore(end)) {
            throw new BusinessException("预约时间不合法，开始时间必须早于结束时间");
        }

        boolean overlap = lambdaQuery()
                .eq(Reservation::getUserId, currentUserId)
                .eq(Reservation::getVenueId, request.getVenueId())
                .ne(Reservation::getStatus, "CANCELED")
                .lt(Reservation::getStartTime, end)
                .gt(Reservation::getEndTime, start)
                .exists();
        if (overlap) {
            throw new BusinessException("预约冲突：该用户在该场地已有重叠预约");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(currentUserId);
        reservation.setVenueId(request.getVenueId());
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setStatus("BOOKED");
        save(reservation);
        return reservation;
    }
}
