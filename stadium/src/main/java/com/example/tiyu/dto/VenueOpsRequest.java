package com.example.tiyu.dto;

import jakarta.validation.constraints.NotBlank;

public class VenueOpsRequest {
    @NotBlank(message = "维护状态不能为空")
    private String maintenanceStatus;

    @NotBlank(message = "清洁状态不能为空")
    private String cleaningStatus;

    @NotBlank(message = "灯光状态不能为空")
    private String lightingStatus;

    @NotBlank(message = "器材状态不能为空")
    private String equipmentStatus;

    private String responsiblePerson;
    private String contactPhone;
    private String remark;

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
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
