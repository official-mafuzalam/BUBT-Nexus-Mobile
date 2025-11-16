package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NoticesResponse {
    @SerializedName("data")
    private List<Notice> data;

    public List<Notice> getData() {
        return data;
    }
}