package com.example.tiyu.dto;

public class VenueAvailabilityResponse {
    private Long venueId;
    private boolean available;
    private String status;
    private String reason;

    public VenueAvailabilityResponse() {
    }

    public VenueAvailabilityResponse(Long venueId, boolean available, String status, String reason) {
        this.venueId = venueId;
        this.available = available;
        this.status = status;
        this.reason = reason;
    }

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
