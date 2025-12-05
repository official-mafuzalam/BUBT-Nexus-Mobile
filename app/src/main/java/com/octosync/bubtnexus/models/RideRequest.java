package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideRequest {
    @SerializedName("requested_seats")
    private int requestedSeats;

    @SerializedName("message")
    private String message;

    public RideRequest(int requestedSeats, String message) {
        this.requestedSeats = requestedSeats;
        this.message = message;
    }

    // Getters
    public int getRequestedSeats() { return requestedSeats; }
    public String getMessage() { return message; }
}