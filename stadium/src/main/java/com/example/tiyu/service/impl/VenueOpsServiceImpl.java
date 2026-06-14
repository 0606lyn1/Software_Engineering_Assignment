package com.example.tiyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tiyu.dto.VenueAvailabilityResponse;
import com.example.tiyu.dto.VenueOpsRequest;
import com.example.tiyu.entity.Venue;
import com.example.tiyu.entity.VenueOps;
import com.example.tiyu.exception.BusinessException;
import com.example.tiyu.mapper.VenueOpsMapper;
import com.example.tiyu.service.VenueOpsService;
import com.example.tiyu.service.VenueService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class VenueOpsServiceImpl extends ServiceImpl<VenueOpsMapper, VenueOps> implements VenueOpsService {
    private static final int CLEANING_EXPIRE_DAYS = 3;
    private static final Set<String> BLOCKING_MAINTENANCE = Set.of("MAINTENANCE", "CLOSED");
    private static final Set<String> BLOCKING_CLEANING = Set.of("NEED_CLEANING", "PENDING_RECHECK");
    private static final Set<String> BLOCKING_LIGHTING = Set.of("FAULT");
    private static final Set<String> BLOCKING_EQUIPMENT = Set.of("MISSING", "DAMAGED");

    private final VenueService venueService;

    public VenueOpsServiceImpl(VenueService venueService) {
        this.venueService = venueService;
    }

    @Override
    public List<VenueOps> listOps(Long venueId) {
        if (venueId == null) {
            return lambdaQuery().orderByDesc(VenueOps::getLastCheckedAt).list();
        }
        return lambdaQuery().eq(VenueOps::getVenueId, venueId).list();
    }

    @Override
    public VenueOps getOrCreateByVenueId(Long venueId) {
        Venue venue = venueService.getById(venueId);
        if (venue == null) {
            throw new BusinessException("场馆不存在");
        }

        VenueOps existing = lambdaQuery().eq(VenueOps::getVenueId, venueId).one();
        if (existing != null) {
            return existing;
        }

        VenueOps ops = new VenueOps();
        ops.setVenueId(venueId);
        ops.setMaintenanceStatus("NORMAL");
        ops.setCleaningStatus("CLEAN");
        ops.setLightingStatus("NORMAL");
        ops.setEquipmentStatus("COMPLETE");
        ops.setResponsiblePerson("场馆管理员");
        ops.setContactPhone("");
        ops.setLastInspector("system");
        ops.setLastCheckedAt(LocalDateTime.now());
        ops.setRemark("系统默认开放状态");
        try {
            save(ops);
            return ops;
        } catch (DuplicateKeyException ignored) {
            VenueOps createdByOtherRequest = lambdaQuery().eq(VenueOps::getVenueId, venueId).one();
            if (createdByOtherRequest != null) {
                return createdByOtherRequest;
            }
            throw ignored;
        }
    }

    @Override
    public VenueOps updateOps(Long venueId, VenueOpsRequest request, String operatorName) {
        VenueOps ops = getOrCreateByVenueId(venueId);
        ops.setMaintenanceStatus(request.getMaintenanceStatus());
        ops.setCleaningStatus(request.getCleaningStatus());
        ops.setLightingStatus(request.getLightingStatus());
        ops.setEquipmentStatus(request.getEquipmentStatus());
        ops.setResponsiblePerson(request.getResponsiblePerson());
        ops.setContactPhone(request.getContactPhone());
        ops.setRemark(request.getRemark());
        ops.setLastInspector(operatorName == null || operatorName.isBlank() ? "unknown" : operatorName);
        ops.setLastCheckedAt(LocalDateTime.now());
        updateById(ops);
        return ops;
    }

    @Override
    public VenueAvailabilityResponse getAvailability(Long venueId) {
        VenueOps ops = getOrCreateByVenueId(venueId);
        if (BLOCKING_MAINTENANCE.contains(ops.getMaintenanceStatus())) {
            return new VenueAvailabilityResponse(venueId, false, ops.getMaintenanceStatus(), "场馆处于维护或闭馆状态");
        }
        if (BLOCKING_CLEANING.contains(ops.getCleaningStatus())) {
            return new VenueAvailabilityResponse(venueId, false, ops.getCleaningStatus(), "清洁状态未达开放标准");
        }
        if (isCleaningExpired(ops)) {
            return new VenueAvailabilityResponse(venueId, false, "CLEANING_EXPIRED", "超过 3 天未完成清扫巡检，已自动暂停预约");
        }
        if (BLOCKING_LIGHTING.contains(ops.getLightingStatus())) {
            return new VenueAvailabilityResponse(venueId, false, ops.getLightingStatus(), "灯光故障待处理");
        }
        if (BLOCKING_EQUIPMENT.contains(ops.getEquipmentStatus())) {
            return new VenueAvailabilityResponse(venueId, false, ops.getEquipmentStatus(), "器材缺失或损坏");
        }
        return new VenueAvailabilityResponse(venueId, true, "AVAILABLE", "运维状态正常，可进入预约流程");
    }

    private boolean isCleaningExpired(VenueOps ops) {
        return "CLEAN".equals(ops.getCleaningStatus())
                && (ops.getLastCheckedAt() == null
                || ops.getLastCheckedAt().isBefore(LocalDateTime.now().minusDays(CLEANING_EXPIRE_DAYS)));
    }
}
