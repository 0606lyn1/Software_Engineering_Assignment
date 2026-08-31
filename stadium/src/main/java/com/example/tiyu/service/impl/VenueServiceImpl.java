package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.dto.VenueRequest;
import com.example.tiyu.entity.Reservation;
import com.example.tiyu.entity.User;
import com.example.tiyu.entity.Venue;
import com.example.tiyu.entity.VenueOps;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.mapper.ReservationMapper;
import com.example.tiyu.mapper.VenueMapper;
import com.example.tiyu.security.RoleNames;
import com.example.tiyu.service.UserService;
import com.example.tiyu.service.VenueOpsService;
import com.example.tiyu.service.VenueService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VenueServiceImpl extends ServiceImpl<VenueMapper, Venue> implements VenueService {

    private final UserService userService;
    private final ReservationMapper reservationMapper;
    private final VenueOpsService venueOpsService;

    public VenueServiceImpl(UserService userService,
                            ReservationMapper reservationMapper,
                            @Lazy VenueOpsService venueOpsService) {
        this.userService = userService;
        this.reservationMapper = reservationMapper;
        this.venueOpsService = venueOpsService;
    }

    @Override
    @Transactional
    public Venue createVenue(VenueRequest request) {
        Venue venue = new Venue();
        applyRequest(venue, request);
        save(venue);
        syncManagerToOps(venue);
        return venue;
    }

    @Override
    @Transactional
    public Venue updateVenue(Long id, VenueRequest request) {
        Venue venue = getById(id);
        if (venue == null) {
            throw new BusinessException("场馆不存在");
        }
        applyRequest(venue, request);
        updateById(venue);
        syncManagerToOps(venue);
        return venue;
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        Venue venue = getById(id);
        if (venue == null) {
            throw new BusinessException("场馆不存在");
        }
        LambdaQueryWrapper<Reservation> query = new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getVenueId, id);
        if (reservationMapper.selectCount(query) > 0) {
            throw new BusinessException("该场馆已有预约记录，不能直接删除");
        }
        removeById(id);
    }

    private void applyRequest(Venue venue, VenueRequest request) {
        venue.setName(request.getName());
        venue.setTypeId(request.getTypeId());
        venue.setPrice(request.getPrice());
        venue.setDescription(request.getDescription());
        venue.setNotes(request.getNotes());
        venue.setManagerUserId(resolveManager(request.getManagerUserId()));
    }

    private Long resolveManager(Long managerUserId) {
        if (managerUserId == null) {
            return null;
        }
        User manager = userService.getById(managerUserId);
        if (manager == null) {
            throw new BusinessException("所选负责人不存在");
        }
        String role = RoleNames.normalize(manager.getRole());
        if (!RoleNames.STAFF.equals(role) && !RoleNames.ADMIN.equals(role)) {
            throw new BusinessException("只能选择场地负责人或管理员作为场馆负责人");
        }
        return managerUserId;
    }

    /**
     * 场馆负责人是运维台账责任人的来源，绑定后台账需要同步，否则维护页会显示旧的责任人。
     */
    private void syncManagerToOps(Venue venue) {
        if (venue.getManagerUserId() == null) {
            return;
        }
        User manager = userService.getById(venue.getManagerUserId());
        if (manager == null) {
            return;
        }
        VenueOps ops = venueOpsService.getOrCreateByVenueId(venue.getId());
        ops.setResponsiblePerson(manager.getUsername());
        ops.setContactPhone(manager.getEmail() == null ? "" : manager.getEmail());
        venueOpsService.updateById(ops);
    }
}
