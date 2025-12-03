// models/MyRidesResponse.java
package com.octosync.bubtnexus.models;

import java.util.List;

public class MyRidesResponse {
    private String status;
    private String message;
    private Data data;

    public static class Data {
        private List<Ride> as_driver;
        private List<Ride> as_passenger;

        // Getters and setters
        public List<Ride> getAsDriver() { return as_driver; }
        public void setAsDriver(List<Ride> as_driver) { this.as_driver = as_driver; }

        public List<Ride> getAsPassenger() { return as_passenger; }
        public void setAsPassenger(List<Ride> as_passenger) { this.as_passenger = as_passenger; }
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
}