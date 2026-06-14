package com.example.tiyu.dto;

import jakarta.validation.constraints.NotBlank;

public class AppealRequest {
    @NotBlank(message = "申诉原因不能为空")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
