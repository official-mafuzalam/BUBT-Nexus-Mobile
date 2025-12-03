package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideRequestRequest {
    @SerializedName("requested_seats")
    private int requestedSeats;

    private String message;

    // Constructors
    public RideRequestRequest() {}

    public RideRequestRequest(int requestedSeats) {
        this.requestedSeats = requestedSeats;
    }

    public RideRequestRequest(int requestedSeats, String message) {
        this.requestedSeats = requestedSeats;
        this.message = message;
    }

    // Getters and setters
    public int getRequestedSeats() { return requestedSeats; }
    public void setRequestedSeats(int requestedSeats) { this.requestedSeats = requestedSeats; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}