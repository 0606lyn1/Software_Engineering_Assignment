package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.entity.ReservationRule;

public interface ReservationRuleService extends IService<ReservationRule> {
    ReservationRule getEffectiveRule(Long venueId);
}
