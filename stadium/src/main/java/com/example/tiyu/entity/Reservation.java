package com.example.tiyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.List;

@TableName("t_reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("venue_id")
    private Long venueId;
    @TableField("start_time")
    private LocalDateTime startTime;
    @TableField("end_time")
    private LocalDateTime endTime;
    private String status;
    @TableField("checkin_code")
    private String checkinCode;
    @TableField("checked_in_at")
    private LocalDateTime checkedInAt;
    @TableField("cancel_deadline")
    private LocalDateTime cancelDeadline;
    @TableField("cancel_reason")
    private String cancelReason;
    @TableField("appeal_reason")
    private String appealReason;
    @TableField("appeal_status")
    private String appealStatus;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField(exist = false)
    private List<String> reminderStatuses;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCheckinCode() { return checkinCode; }
    public void setCheckinCode(String checkinCode) { this.checkinCode = checkinCode; }
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
    public LocalDateTime getCancelDeadline() { return cancelDeadline; }
    public void setCancelDeadline(LocalDateTime cancelDeadline) { this.cancelDeadline = cancelDeadline; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public String getAppealReason() { return appealReason; }
    public void setAppealReason(String appealReason) { this.appealReason = appealReason; }
    public String getAppealStatus() { return appealStatus; }
    public void setAppealStatus(String appealStatus) { this.appealStatus = appealStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<String> getReminderStatuses() { return reminderStatuses; }
    public void setReminderStatuses(List<String> reminderStatuses) { this.reminderStatuses = reminderStatuses; }
}
