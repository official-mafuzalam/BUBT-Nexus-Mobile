package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Rent data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Rent getData() { return data; }
    public void setData(Rent data) { this.data = data; }
}
