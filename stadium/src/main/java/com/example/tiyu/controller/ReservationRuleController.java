package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.ReservationRuleRequest;
import com.example.tiyu.entity.ReservationRule;
import com.example.tiyu.service.ReservationRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation-rules")
public class ReservationRuleController {
    private final ReservationRuleService ruleService;

    public ReservationRuleController(ReservationRuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    @Operation(summary = "预约规则列表")
    public ApiResponse<List<ReservationRule>> list() {
        return ApiResponse.success(ruleService.list());
    }

    @GetMapping("/effective")
    @Operation(summary = "生效预约规则")
    public ApiResponse<ReservationRule> effective(@RequestParam(required = false) Long venueId) {
        return ApiResponse.success(ruleService.getEffectiveRule(venueId));
    }

    @PostMapping
    @Operation(summary = "保存预约规则")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReservationRule> save(@Valid @RequestBody ReservationRuleRequest request) {
        ReservationRule rule = request.getVenueId() == null
                ? ruleService.lambdaQuery().isNull(ReservationRule::getVenueId).one()
                : ruleService.lambdaQuery().eq(ReservationRule::getVenueId, request.getVenueId()).one();
        if (rule == null) {
            rule = new ReservationRule();
        }
        rule.setVenueId(request.getVenueId());
        rule.setAdvanceDays(request.getAdvanceDays());
        rule.setCancelBeforeHours(request.getCancelBeforeHours());
        rule.setMaxHoursPerBooking(request.getMaxHoursPerBooking());
        rule.setDailyLimit(request.getDailyLimit());
        rule.setWeeklyLimit(request.getWeeklyLimit());
        rule.setOpenTime(request.getOpenTime());
        rule.setCloseTime(request.getCloseTime());
        rule.setSlotMinutes(request.getSlotMinutes());
        ruleService.saveOrUpdate(rule);
        return ApiResponse.success("规则已保存", rule);
    }
}
