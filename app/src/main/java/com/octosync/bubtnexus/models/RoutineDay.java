package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoutineDay {
    @SerializedName("day")
    private String day;

    @SerializedName("classes")
    private List<ClassItem> classes;

    // Getters and Setters
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<ClassItem> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassItem> classes) {
        this.classes = classes;
    }
}