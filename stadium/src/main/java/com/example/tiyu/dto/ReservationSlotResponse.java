package com.example.tiyu.dto;

import java.time.LocalDateTime;

public class ReservationSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String label;

    public ReservationSlotResponse() {
    }

    public ReservationSlotResponse(LocalDateTime startTime, LocalDateTime endTime, String status, String label) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.label = label;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
