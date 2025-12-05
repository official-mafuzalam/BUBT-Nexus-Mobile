package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Ride {
    @SerializedName("id")
    private int id;

    @SerializedName("driver_id")
    private int driverId;

    @SerializedName("from_location")
    private String fromLocation;

    @SerializedName("to_location")
    private String toLocation;

    @SerializedName("from_lat")
    private String fromLat;

    @SerializedName("from_lng")
    private String fromLng;

    @SerializedName("to_lat")
    private String toLat;

    @SerializedName("to_lng")
    private String toLng;

    @SerializedName("available_seats")
    private int availableSeats;

    @SerializedName("total_seats")
    private int totalSeats;

    @SerializedName("fare_per_seat")
    private String farePerSeat;

    @SerializedName("status")
    private String status;

    @SerializedName("departure_time")
    private String departureTime;

    @SerializedName("notes")
    private String notes;

    @SerializedName("distance")
    private double distance;

    @SerializedName("driver")
    private User driver;

    @SerializedName("confirmed_passengers")
    private List<Object> confirmedPassengers; // Changed back to List<Object>

    // Add these fields to your existing Ride.java
    @SerializedName("vehicle_type")
    private String vehicleType;

    @SerializedName("vehicle_number")
    private String vehicleNumber;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("requests")
    private List<PassengerRequest> requests;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public String getFromLat() { return fromLat; }
    public void setFromLat(String fromLat) { this.fromLat = fromLat; }

    public String getFromLng() { return fromLng; }
    public void setFromLng(String fromLng) { this.fromLng = fromLng; }

    public String getToLat() { return toLat; }
    public void setToLat(String toLat) { this.toLat = toLat; }

    public String getToLng() { return toLng; }
    public void setToLng(String toLng) { this.toLng = toLng; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getFarePerSeat() { return farePerSeat; }
    public void setFarePerSeat(String farePerSeat) { this.farePerSeat = farePerSeat; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public List<Object> getConfirmedPassengers() { return confirmedPassengers; }
    public void setConfirmedPassengers(List<Object> confirmedPassengers) { this.confirmedPassengers = confirmedPassengers; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<PassengerRequest> getRequests() { return requests; }
    public void setRequests(List<PassengerRequest> requests) { this.requests = requests; }

    // Helper method to get confirmed passengers count
    public int getConfirmedPassengersCount() {
        return confirmedPassengers != null ? confirmedPassengers.size() : 0;
    }

    // Helper method to calculate booked seats
    public int getBookedSeats() {
        return totalSeats - availableSeats;
    }
}