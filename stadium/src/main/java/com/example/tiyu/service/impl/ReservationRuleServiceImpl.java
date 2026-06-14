package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.entity.ReservationRule;
import com.example.tiyu.mapper.ReservationRuleMapper;
import com.example.tiyu.service.ReservationRuleService;
import org.springframework.stereotype.Service;

@Service
public class ReservationRuleServiceImpl extends ServiceImpl<ReservationRuleMapper, ReservationRule> implements ReservationRuleService {
    @Override
    public ReservationRule getEffectiveRule(Long venueId) {
        ReservationRule venueRule = lambdaQuery().eq(ReservationRule::getVenueId, venueId).one();
        if (venueRule != null) {
            return venueRule;
        }
        ReservationRule globalRule = lambdaQuery().isNull(ReservationRule::getVenueId).one();
        if (globalRule != null) {
            return globalRule;
        }
        ReservationRule fallback = new ReservationRule();
        fallback.setAdvanceDays(3);
        fallback.setCancelBeforeHours(4);
        fallback.setMaxHoursPerBooking(2);
        fallback.setDailyLimit(2);
        fallback.setWeeklyLimit(6);
        fallback.setOpenTime("06:00");
        fallback.setCloseTime("22:00");
        fallback.setSlotMinutes(60);
        return fallback;
    }
}
