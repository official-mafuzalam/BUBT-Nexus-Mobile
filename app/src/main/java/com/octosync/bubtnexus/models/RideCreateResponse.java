package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideCreateResponse {
    private String status;
    private String message;
    private Ride data;

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Ride getData() { return data; }
    public void setData(Ride data) { this.data = data; }
}