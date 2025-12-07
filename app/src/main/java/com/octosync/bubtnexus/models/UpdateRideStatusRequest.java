package com.octosync.bubtnexus.models;

public class UpdateRideStatusRequest {
    private String status;

    public UpdateRideStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}