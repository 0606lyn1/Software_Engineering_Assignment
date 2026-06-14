package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.AnnouncementRequest;
import com.example.tiyu.entity.Announcement;
import com.example.tiyu.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    @Operation(summary = "公告列表")
    public ApiResponse<List<Announcement>> list() {
        return ApiResponse.success(announcementService.lambdaQuery()
                .orderByDesc(Announcement::getCreatedAt)
                .last("LIMIT 6")
                .list());
    }

    @PostMapping
    @Operation(summary = "发布公告")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Announcement> create(@Valid @RequestBody AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setLevel(request.getLevel() == null ? "INFO" : request.getLevel());
        announcement.setCreatedAt(LocalDateTime.now());
        announcementService.save(announcement);
        return ApiResponse.success("公告已发布", announcement);
    }
}
