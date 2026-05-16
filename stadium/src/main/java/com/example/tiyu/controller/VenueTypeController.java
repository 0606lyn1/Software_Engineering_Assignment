package com.example.tiyu.controller;

import com.example.tiyu.common.ApiResponse;
import com.example.tiyu.entity.VenueType;
import com.example.tiyu.service.VenueTypeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/venue-types")
public class VenueTypeController {

    private final VenueTypeService venueTypeService;

    public VenueTypeController(VenueTypeService venueTypeService) {
        this.venueTypeService = venueTypeService;
    }

    @GetMapping
    @Operation(summary = "场馆类型")
    public ApiResponse<List<VenueType>> list() {
        return ApiResponse.success(venueTypeService.list());
    }
}
