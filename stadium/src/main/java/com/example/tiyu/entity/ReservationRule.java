package com.example.tiyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_reservation_rule")
public class ReservationRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("venue_id")
    private Long venueId;
    @TableField("advance_days")
    private Integer advanceDays;
    @TableField("cancel_before_hours")
    private Integer cancelBeforeHours;
    @TableField("max_hours_per_booking")
    private Integer maxHoursPerBooking;
    @TableField("daily_limit")
    private Integer dailyLimit;
    @TableField("weekly_limit")
    private Integer weeklyLimit;
    @TableField("open_time")
    private String openTime;
    @TableField("close_time")
    private String closeTime;
    @TableField("slot_minutes")
    private Integer slotMinutes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
