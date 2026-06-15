package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.entity.ReminderDelivery;
import com.example.tiyu.entity.Reservation;

import java.util.List;

public interface ReminderDeliveryService extends IService<ReminderDelivery> {
    void processUpcomingReminders();
    void attachStatuses(List<Reservation> reservations);
}
