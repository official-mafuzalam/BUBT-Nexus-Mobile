package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideDetailsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Ride ride;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
}