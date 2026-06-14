package com.example.tiyu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReservationRuleRequest {
    private Long venueId;
    @NotNull(message = "提前预约天数不能为空")
    @Min(value = 1, message = "提前预约天数至少 1 天")
    @Max(value = 30, message = "提前预约天数不能超过 30 天")
    private Integer advanceDays;
    @NotNull(message = "取消截止小时不能为空")
    @Min(value = 0, message = "取消截止小时不能小于 0")
    private Integer cancelBeforeHours;
    @NotNull(message = "单次最长小时不能为空")
    @Min(value = 1, message = "单次最长至少 1 小时")
    private Integer maxHoursPerBooking;
    @NotNull(message = "每日预约次数不能为空")
    @Min(value = 1, message = "每日预约次数至少 1 次")
    private Integer dailyLimit;
    @NotNull(message = "每周预约次数不能为空")
    @Min(value = 1, message = "每周预约次数至少 1 次")
    private Integer weeklyLimit;
    @NotBlank(message = "开放时间不能为空")
    private String openTime;
    @NotBlank(message = "关闭时间不能为空")
    private String closeTime;
    @NotNull(message = "时段粒度不能为空")
    @Min(value = 30, message = "时段粒度至少 30 分钟")
    private Integer slotMinutes;

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public Integer getAdvanceDays() { return advanceDays; }
    public void setAdvanceDays(Integer advanceDays) { this.advanceDays = advanceDays; }
    public Integer getCancelBeforeHours() { return cancelBeforeHours; }
    public void setCancelBeforeHours(Integer cancelBeforeHours) { this.cancelBeforeHours = cancelBeforeHours; }
    public Integer getMaxHoursPerBooking() { return maxHoursPerBooking; }
    public void setMaxHoursPerBooking(Integer maxHoursPerBooking) { this.maxHoursPerBooking = maxHoursPerBooking; }
    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }
    public Integer getWeeklyLimit() { return weeklyLimit; }
    public void setWeeklyLimit(Integer weeklyLimit) { this.weeklyLimit = weeklyLimit; }
    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
    public Integer getSlotMinutes() { return slotMinutes; }
    public void setSlotMinutes(Integer slotMinutes) { this.slotMinutes = slotMinutes; }
}
