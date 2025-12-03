package com.octosync.bubtnexus.models;

import java.util.List;

public class RidesResponse {
    private String status;
    private String message;
    private List<Ride> data;

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Ride> getData() { return data; }
    public void setData(List<Ride> data) { this.data = data; }
}