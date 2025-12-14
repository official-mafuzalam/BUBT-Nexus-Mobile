package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentSetAvailabilityRequest {
    @SerializedName("is_available")
    private boolean isAvailable;

    public RentSetAvailabilityRequest(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
