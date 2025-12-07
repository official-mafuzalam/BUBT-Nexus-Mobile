package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("email_verified_at")
    private String emailVerifiedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("last_seen_at")
    private String lastSeenAt;

    @SerializedName("online_status")
    private String onlineStatus;

    @SerializedName("last_seen_text")
    private String lastSeenText;

    @SerializedName("last_seen_detailed")
    private LastSeenDetailed lastSeenDetailed;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("is_student")
    private boolean isStudent;

    @SerializedName("is_faculty")
    private boolean isFaculty;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("details")
    private UserDetails details;

    @SerializedName("roles")
    private List<String> roles;

    @SerializedName("permissions")
    private List<String> permissions;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getEmailVerifiedAt() { return emailVerifiedAt; }
    public String getStatus() { return status; }
    public String getLastSeenAt() { return lastSeenAt; }
    public String getOnlineStatus() { return onlineStatus; }
    public String getLastSeenText() { return lastSeenText; }
    public LastSeenDetailed getLastSeenDetailed() { return lastSeenDetailed; }
    public String getUserType() { return userType; }
    public boolean isStudent() { return isStudent; }
    public boolean isFaculty() { return isFaculty; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public UserDetails getDetails() { return details; }
    public List<String> getRoles() { return roles; }
    public List<String> getPermissions() { return permissions; }

    public static class LastSeenDetailed {
        @SerializedName("text")
        private String text;

        @SerializedName("icon")
        private String icon;

        @SerializedName("color")
        private String color;

        public String getText() { return text; }
        public String getIcon() { return icon; }
        public String getColor() { return color; }
    }

    public static class UserDetails {
        @SerializedName("id")
        private int id;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("is_verified")
        private String isVerified;

        @SerializedName("semester")
        private String semester;

        @SerializedName("intake")
        private int intake;

        @SerializedName("section")
        private int section;

        @SerializedName("student_id")
        private String studentId;

        @SerializedName("cgpa")
        private String cgpa;

        @SerializedName("department")
        private String department;

        @SerializedName("faculty_code")
        private String facultyCode;

        @SerializedName("designation")
        private String designation;

        @SerializedName("phone")
        private String phone;

        @SerializedName("profile_picture")
        private String profilePicture;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("user_type")
        private String userType;

        @SerializedName("program")
        private Program program;  // Added Program object

        // Getters
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getIsVerified() { return isVerified; }
        public String getSemester() { return semester; }
        public int getIntake() { return intake; }
        public int getSection() { return section; }
        public String getStudentId() { return studentId; }
        public String getCgpa() { return cgpa; }
        public String getDepartment() { return department; }
        public String getFacultyCode() { return facultyCode; }
        public String getDesignation() { return designation; }
        public String getPhone() { return phone; }
        public String getProfilePicture() { return profilePicture; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getUserType() { return userType; }
        public Program getProgram() { return program; }  // Getter for program
    }

    public static class Program {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("code")
        private String code;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCode() { return code; }
    }
}