package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.dto.VenueAvailabilityResponse;
import com.example.tiyu.dto.VenueOpsRequest;
import com.example.tiyu.entity.VenueOps;

import java.util.List;

public interface VenueOpsService extends IService<VenueOps> {
    List<VenueOps> listOps(Long venueId);
    VenueOps getOrCreateByVenueId(Long venueId);
    VenueOps updateOps(Long venueId, VenueOpsRequest request, String operatorName);
    VenueAvailabilityResponse getAvailability(Long venueId);
}
