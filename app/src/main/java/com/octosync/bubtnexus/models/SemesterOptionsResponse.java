package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class SemesterOptionsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Map<String, String> data; // Key: "Fall 2025", Value: "611"

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}