package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class CreateRideRequest {
    @SerializedName("from_location")
    private String fromLocation;

    @SerializedName("to_location")
    private String toLocation;

    @SerializedName("from_lat")
    private double fromLat;

    @SerializedName("from_lng")
    private double fromLng;

    @SerializedName("to_lat")
    private double toLat;

    @SerializedName("to_lng")
    private double toLng;

    @SerializedName("total_seats")
    private int totalSeats;

    @SerializedName("fare_per_seat")
    private double farePerSeat;

    @SerializedName("departure_time")
    private String departureTime;

    private String notes;

    @SerializedName("vehicle_type")
    private String vehicleType;

    @SerializedName("vehicle_number")
    private String vehicleNumber;

    // Constructors
    public CreateRideRequest() {}

    public CreateRideRequest(String fromLocation, String toLocation, double fromLat, double fromLng,
                             double toLat, double toLng, int totalSeats, double farePerSeat,
                             String departureTime) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.fromLat = fromLat;
        this.fromLng = fromLng;
        this.toLat = toLat;
        this.toLng = toLng;
        this.totalSeats = totalSeats;
        this.farePerSeat = farePerSeat;
        this.departureTime = departureTime;
    }

    // Getters and setters
    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public double getFromLat() { return fromLat; }
    public void setFromLat(double fromLat) { this.fromLat = fromLat; }

    public double getFromLng() { return fromLng; }
    public void setFromLng(double fromLng) { this.fromLng = fromLng; }

    public double getToLat() { return toLat; }
    public void setToLat(double toLat) { this.toLat = toLat; }

    public double getToLng() { return toLng; }
    public void setToLng(double toLng) { this.toLng = toLng; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public double getFarePerSeat() { return farePerSeat; }
    public void setFarePerSeat(double farePerSeat) { this.farePerSeat = farePerSeat; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
}