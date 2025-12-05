package com.octosync.bubtnexus.models;

public class PassengerRequestResponse {
    private String status;
    private String message;
    private PassengerRequest data;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public PassengerRequest getData() { return data; }
    public void setData(PassengerRequest data) { this.data = data; }
}