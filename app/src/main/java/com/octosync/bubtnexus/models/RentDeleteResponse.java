package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentDeleteResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
