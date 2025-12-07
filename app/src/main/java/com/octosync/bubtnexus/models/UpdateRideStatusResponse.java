package com.octosync.bubtnexus.models;

public class UpdateRideStatusResponse {
    private boolean success;
    private String message;
    private Ride data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
}