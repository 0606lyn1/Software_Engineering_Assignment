package com.example.tiyu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentCreateRequest {
    @NotNull(message = "场馆ID不能为空")
    private Long venueId;
    @NotBlank(message = "评论内容不能为空")
    private String content;

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
