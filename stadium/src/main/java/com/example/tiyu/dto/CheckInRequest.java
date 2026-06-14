package com.example.tiyu.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {
    @NotBlank(message = "核销码不能为空")
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
