package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.VenueRequest;
import com.example.tiyu.entity.Venue;
import com.example.tiyu.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    @Operation(summary = "场馆列表")
    public ApiResponse<List<Venue>> list(@RequestParam(required = false) Long typeId) {
        List<Venue> data = typeId == null
                ? venueService.list()
                : venueService.lambdaQuery().eq(Venue::getTypeId, typeId).list();
        return ApiResponse.success(data);
    }

    @PostMapping
    @Operation(summary = "新增场馆")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Venue> create(@Valid @RequestBody VenueRequest request) {
        return ApiResponse.success("创建成功", venueService.createVenue(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改场馆")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Venue> update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        return ApiResponse.success("更新成功", venueService.updateVenue(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除场馆")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ApiResponse.success("删除成功", null);
    }
}
