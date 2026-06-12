package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.dto.VenueAvailabilityResponse;
import com.example.tiyu.dto.VenueOpsRequest;
import com.example.tiyu.entity.VenueOps;
import com.example.tiyu.service.VenueOpsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venue-ops")
public class VenueOpsController {

    private final VenueOpsService venueOpsService;

    public VenueOpsController(VenueOpsService venueOpsService) {
        this.venueOpsService = venueOpsService;
    }

    @GetMapping
    @Operation(summary = "场地运维台账列表")
    public ApiResponse<List<VenueOps>> list(@RequestParam(required = false) Long venueId) {
        return ApiResponse.success(venueOpsService.listOps(venueId));
    }

    @GetMapping("/{venueId}")
    @Operation(summary = "场地运维详情")
    public ApiResponse<VenueOps> detail(@PathVariable Long venueId) {
        return ApiResponse.success(venueOpsService.getOrCreateByVenueId(venueId));
    }

    @PutMapping("/{venueId}")
    @Operation(summary = "维护人员填报场地状态")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<VenueOps> update(
            @PathVariable Long venueId,
            @Valid @RequestBody VenueOpsRequest request,
            Authentication authentication) {
        String operatorName = authentication == null ? null : authentication.getName();
        return ApiResponse.success("填报成功", venueOpsService.updateOps(venueId, request, operatorName));
    }

    @GetMapping("/{venueId}/availability")
    @Operation(summary = "场地可约状态")
    public ApiResponse<VenueAvailabilityResponse> availability(@PathVariable Long venueId) {
        return ApiResponse.success(venueOpsService.getAvailability(venueId));
    }
}
