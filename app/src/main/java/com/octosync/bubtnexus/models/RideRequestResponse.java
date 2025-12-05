package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideRequestResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private RequestData data;

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public RequestData getData() { return data; }

    public static class RequestData {
        @SerializedName("id")
        private int id;

        @SerializedName("ride_id")
        private int rideId;

        @SerializedName("passenger_id")
        private int passengerId;

        @SerializedName("requested_seats")
        private int requestedSeats;

        @SerializedName("message")
        private String message;

        @SerializedName("status")
        private String status;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("passenger")
        private User passenger;

        // Getters
        public int getId() { return id; }
        public int getRideId() { return rideId; }
        public int getPassengerId() { return passengerId; }
        public int getRequestedSeats() { return requestedSeats; }
        public String getMessage() { return message; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
        public User getPassenger() { return passenger; }
    }
}