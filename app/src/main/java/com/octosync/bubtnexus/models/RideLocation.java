package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RideLocation {
    private int id;

    @SerializedName("ride_id")
    private int rideId;

    @SerializedName("user_id")
    private int userId;

    private double latitude;
    private double longitude;
    private Double speed;
    private Double bearing;
    private Double accuracy;

    @SerializedName("recorded_at")
    private String recordedAt;

    private User user;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public Double getBearing() { return bearing; }
    public void setBearing(Double bearing) { this.bearing = bearing; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}