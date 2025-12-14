package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentSetAvailabilityResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Rent data;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Rent getData() { return data; }
    public void setData(Rent data) { this.data = data; }
}
