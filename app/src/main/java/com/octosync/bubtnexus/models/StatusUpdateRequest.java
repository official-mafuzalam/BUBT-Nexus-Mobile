package com.octosync.bubtnexus.models;

public class StatusUpdateRequest {
    private String status;

    // Constructors
    public StatusUpdateRequest() {}

    public StatusUpdateRequest(String status) {
        this.status = status;
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}