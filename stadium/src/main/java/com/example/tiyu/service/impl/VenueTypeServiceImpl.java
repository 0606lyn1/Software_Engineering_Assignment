package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.entity.VenueType;
import com.example.tiyu.mapper.VenueTypeMapper;
import com.example.tiyu.service.VenueTypeService;
import org.springframework.stereotype.Service;

@Service
public class VenueTypeServiceImpl extends ServiceImpl<VenueTypeMapper, VenueType> implements VenueTypeService {
}
