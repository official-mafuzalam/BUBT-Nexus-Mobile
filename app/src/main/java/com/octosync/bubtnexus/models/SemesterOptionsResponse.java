package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemesterOptionsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Object data; // Can be Map or List

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // Helper method to get semester as Map<String, String>
    public Map<String, String> getSemesterMap() {
        if (data instanceof Map) {
            try {
                // Handle different Map types
                Map<?, ?> rawMap = (Map<?, ?>) data;
                Map<String, String> resultMap = new HashMap<>();

                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    resultMap.put(key, value);
                }
                return resultMap;
            } catch (Exception e) {
                e.printStackTrace();
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    // Helper method to get semester as List<String>
    public List<String> getSemesterList() {
        if (data instanceof List) {
            try {
                return (List<String>) data;
            } catch (ClassCastException e) {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
}