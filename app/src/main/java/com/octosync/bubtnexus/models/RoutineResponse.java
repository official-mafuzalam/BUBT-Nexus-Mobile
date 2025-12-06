package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoutineResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("program")
    private String program;

    @SerializedName("program_name")
    private String programName;

    @SerializedName("intake")
    private String intake;

    @SerializedName("section")
    private int section;

    @SerializedName("semester")
    private String semester;

    @SerializedName("routine")
    private List<DailyRoutine> routine;

    @SerializedName("detailed_routines")
    private List<DetailedRoutine> detailedRoutines;

    @SerializedName("meta")
    private Meta meta;

    // Getters
    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public String getProgram() { return program; }
    public String getProgramName() { return programName; }
    public String getIntake() { return intake; }
    public int getSection() { return section; }
    public String getSemester() { return semester; }
    public List<DailyRoutine> getRoutine() { return routine; }
    public List<DetailedRoutine> getDetailedRoutines() { return detailedRoutines; }
    public Meta getMeta() { return meta; }

    public static class DailyRoutine {
        @SerializedName("day")
        private String day;

        // This is a dynamic map since each time slot is a key
        // We'll parse it differently
        private String timeSlot;
        private String classDetails;

        // Constructor to parse the JSON object
        public DailyRoutine() {}

        public String getDay() { return day; }
        public String getTimeSlot() { return timeSlot; }
        public String getClassDetails() { return classDetails; }

        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public void setClassDetails(String classDetails) { this.classDetails = classDetails; }
    }

    public static class DetailedRoutine {
        @SerializedName("id")
        private int id;

        @SerializedName("day")
        private String day;

        @SerializedName("time_slot")
        private String timeSlot;

        @SerializedName("formatted_time")
        private String formattedTime;

        @SerializedName("course_code")
        private String courseCode;

        @SerializedName("course_name")
        private String courseName;

        @SerializedName("teacher_code")
        private String teacherCode;

        @SerializedName("teacher_name")
        private String teacherName;

        @SerializedName("room_number")
        private String roomNumber;

        @SerializedName("room_type")
        private String roomType;

        @SerializedName("class_details")
        private String classDetails;

        @SerializedName("status")
        private String status;

        // Getters
        public int getId() { return id; }
        public String getDay() { return day; }
        public String getTimeSlot() { return timeSlot; }
        public String getFormattedTime() { return formattedTime; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getTeacherCode() { return teacherCode; }
        public String getTeacherName() { return teacherName; }
        public String getRoomNumber() { return roomNumber; }
        public String getRoomType() { return roomType; }
        public String getClassDetails() { return classDetails; }
        public String getStatus() { return status; }
    }

    public static class Meta {
        @SerializedName("total_classes")
        private int totalClasses;

        @SerializedName("days_count")
        private int daysCount;

        @SerializedName("unique_courses")
        private int uniqueCourses;

        @SerializedName("unique_teachers")
        private int uniqueTeachers;

        // Getters
        public int getTotalClasses() { return totalClasses; }
        public int getDaysCount() { return daysCount; }
        public int getUniqueCourses() { return uniqueCourses; }
        public int getUniqueTeachers() { return uniqueTeachers; }
    }
}