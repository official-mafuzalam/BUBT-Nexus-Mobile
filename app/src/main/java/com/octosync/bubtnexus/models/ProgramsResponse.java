package com.octosync.bubtnexus.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProgramsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Program> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Program> getData() {
        return data;
    }

    public void setData(List<Program> data) {
        this.data = data;
    }
}