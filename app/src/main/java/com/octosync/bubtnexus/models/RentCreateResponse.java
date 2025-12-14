package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentCreateResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Rent data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Rent getData() { return data; }
    public void setData(Rent data) { this.data = data; }
}
