package com.example.tiyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tiyu.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}
