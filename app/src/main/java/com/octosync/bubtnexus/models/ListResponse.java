package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<String> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}