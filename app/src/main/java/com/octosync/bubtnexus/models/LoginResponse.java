package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("user")
        private User user;

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public User getUser() {
            return user;
        }
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("user_type")
        private String userType;

        @SerializedName("is_student")
        private boolean isStudent;

        @SerializedName("is_faculty")
        private boolean isFaculty;

        @SerializedName("details")
        private Details details;

        @SerializedName("roles")
        private List<String> roles;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getUserType() {
            return userType;
        }

        public boolean isStudent() {
            return isStudent;
        }

        public boolean isFaculty() {
            return isFaculty;
        }

        public Details getDetails() {
            return details;
        }

        public List<String> getRoles() {
            return roles;
        }
    }

    public static class Details {
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

        @SerializedName("user_type")
        private String userType;

        @SerializedName("program")
        private Program program;

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getIsVerified() {
            return isVerified;
        }

        public String getSemester() {
            return semester;
        }

        public int getIntake() {
            return intake;
        }

        public int getSection() {
            return section;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getCgpa() {
            return cgpa;
        }

        public String getDepartment() {
            return department;
        }

        public String getFacultyCode() {
            return facultyCode;
        }

        public String getDesignation() {
            return designation;
        }

        public String getPhone() {
            return phone;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public String getUserType() {
            return userType;
        }

        public Program getProgram() {
            return program;
        }
    }

    public static class Program {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("code")
        private String code;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }
}