package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideRequest {
    private int id;

    @SerializedName("ride_id")
    private int rideId;

    @SerializedName("passenger_id")
    private int passengerId;

    @SerializedName("passenger_name")
    private String passengerName;

    @SerializedName("requested_seats")
    private int requestedSeats;

    private String status;
    private String message;

    @SerializedName("requested_at")
    private String requestedAt;

    private User passenger;
    private Ride ride;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public int getRequestedSeats() { return requestedSeats; }
    public void setRequestedSeats(int requestedSeats) { this.requestedSeats = requestedSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRequestedAt() { return requestedAt; }
    public void setRequestedAt(String requestedAt) { this.requestedAt = requestedAt; }

    public User getPassenger() { return passenger; }
    public void setPassenger(User passenger) { this.passenger = passenger; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
}