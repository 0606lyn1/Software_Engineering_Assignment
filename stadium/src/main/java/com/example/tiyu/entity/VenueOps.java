package com.example.tiyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_venue_ops")
public class VenueOps {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("venue_id")
    private Long venueId;

    @TableField("maintenance_status")
    private String maintenanceStatus;

    @TableField("cleaning_status")
    private String cleaningStatus;

    @TableField("lighting_status")
    private String lightingStatus;

    @TableField("equipment_status")
    private String equipmentStatus;

    @TableField("responsible_person")
    private String responsiblePerson;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("last_inspector")
    private String lastInspector;

    @TableField("last_checked_at")
    private LocalDateTime lastCheckedAt;

    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getMaintenanceStatus() { return maintenanceStatus; }
    public void setMaintenanceStatus(String maintenanceStatus) { this.maintenanceStatus = maintenanceStatus; }
    public String getCleaningStatus() { return cleaningStatus; }
    public void setCleaningStatus(String cleaningStatus) { this.cleaningStatus = cleaningStatus; }
    public String getLightingStatus() { return lightingStatus; }
    public void setLightingStatus(String lightingStatus) { this.lightingStatus = lightingStatus; }
    public String getEquipmentStatus() { return equipmentStatus; }
    public void setEquipmentStatus(String equipmentStatus) { this.equipmentStatus = equipmentStatus; }
    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getLastInspector() { return lastInspector; }
    public void setLastInspector(String lastInspector) { this.lastInspector = lastInspector; }
    public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }
    public void setLastCheckedAt(LocalDateTime lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
