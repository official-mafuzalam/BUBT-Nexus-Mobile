package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Ride {
    private int id;

    @SerializedName("driver_id")
    private int driverId;

    @SerializedName("driver_name")
    private String driverName;

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

    @SerializedName("available_seats")
    private int availableSeats;

    @SerializedName("total_seats")
    private int totalSeats;

    @SerializedName("fare_per_seat")
    private double farePerSeat;

    private String status;

    @SerializedName("departure_time")
    private String departureTime;

    private String notes;

    @SerializedName("vehicle_type")
    private String vehicleType;

    @SerializedName("vehicle_number")
    private String vehicleNumber;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("confirmed_passengers")
    private List<Passenger> confirmedPassengers;

    private User driver;
    private double distance;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

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

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public double getFarePerSeat() { return farePerSeat; }
    public void setFarePerSeat(double farePerSeat) { this.farePerSeat = farePerSeat; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<Passenger> getConfirmedPassengers() { return confirmedPassengers; }
    public void setConfirmedPassengers(List<Passenger> confirmedPassengers) { this.confirmedPassengers = confirmedPassengers; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
}