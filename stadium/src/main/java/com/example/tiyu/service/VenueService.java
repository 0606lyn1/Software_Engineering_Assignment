package com.example.tiyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tiyu.dto.VenueRequest;
import com.example.tiyu.entity.Venue;

public interface VenueService extends IService<Venue> {
    Venue createVenue(VenueRequest request);

    Venue updateVenue(Long id, VenueRequest request);

    void deleteVenue(Long id);
}
