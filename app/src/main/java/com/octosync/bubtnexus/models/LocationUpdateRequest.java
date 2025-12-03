package com.octosync.bubtnexus.models;

public class LocationUpdateRequest {
    private double latitude;
    private double longitude;
    private Double speed;
    private Double bearing;
    private Double accuracy;

    // Constructors
    public LocationUpdateRequest() {}

    public LocationUpdateRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationUpdateRequest(double latitude, double longitude, Double speed,
                                 Double bearing, Double accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
    }

    // Getters and setters
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
}