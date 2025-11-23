package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class ClassItem {
    @SerializedName("time")
    private String time;

    @SerializedName("course_code")
    private String courseCode;

    @SerializedName("faculty_code")
    private String facultyCode;

    @SerializedName("room")
    private String room;

    // Getters and Setters
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}