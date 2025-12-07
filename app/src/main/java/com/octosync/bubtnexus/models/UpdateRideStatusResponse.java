package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class UpdateRideStatusResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Ride data;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Ride getData() {
        return data;
    }

    public void setData(Ride data) {
        this.data = data;
    }

    // Helper method to check if success
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}