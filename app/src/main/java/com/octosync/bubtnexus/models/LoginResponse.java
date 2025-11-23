package com.octosync.bubtnexus.models;

import java.util.List;

public class LoginResponse {
    private boolean success;
    private String message;
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
        private String access_token;
        private String token_type;
        private User user;

        public String getAccessToken() {
            return access_token;
        }

        public String getTokenType() {
            return token_type;
        }

        public User getUser() {
            return user;
        }
    }

    public static class User {
        private int id;
        private String name;
        private String email;
        private String email_verified_at;
        private String status;
        private String last_seen_at;
        private String online_status;
        private String last_seen_text;
        private LastSeenDetailed last_seen_detailed;
        private String user_type;
        private boolean is_student;
        private boolean is_faculty;
        private String created_at;
        private String updated_at;
        private UserDetails details;
        private List<String> roles;
        private List<String> permissions;

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getEmailVerifiedAt() { return email_verified_at; }
        public String getStatus() { return status; }
        public String getLastSeenAt() { return last_seen_at; }
        public String getOnlineStatus() { return online_status; }
        public String getLastSeenText() { return last_seen_text; }
        public LastSeenDetailed getLastSeenDetailed() { return last_seen_detailed; }
        public String getUserType() { return user_type; }
        public boolean isStudent() { return is_student; }
        public boolean isFaculty() { return is_faculty; }
        public String getCreatedAt() { return created_at; }
        public String getUpdatedAt() { return updated_at; }
        public UserDetails getDetails() { return details; }
        public List<String> getRoles() { return roles; }
        public List<String> getPermissions() { return permissions; }

        public static class LastSeenDetailed {
            private String text;
            private String icon;
            private String color;

            public String getText() { return text; }
            public String getIcon() { return icon; }
            public String getColor() { return color; }
        }

        public static class UserDetails {
            private int id;
            private int user_id;
            private String semester;
            private String intake;
            private String program;
            private String student_id;
            private Double cgpa;
            private String department;
            private String faculty_id;
            private String designation;
            private String office_room;
            private String office_hours;
            private String phone;
            private String address;
            private String date_of_birth;
            private String emergency_contact;
            private String profile_picture;
            private String created_at;
            private String updated_at;

            // Getters
            public int getId() { return id; }
            public int getUserId() { return user_id; }
            public String getSemester() { return semester; }
            public String getIntake() { return intake; }
            public String getProgram() { return program; }
            public String getStudentId() { return student_id; }
            public Double getCgpa() { return cgpa; }
            public String getDepartment() { return department; }
            public String getFacultyId() { return faculty_id; }
            public String getDesignation() { return designation; }
            public String getOfficeRoom() { return office_room; }
            public String getOfficeHours() { return office_hours; }
            public String getPhone() { return phone; }
            public String getAddress() { return address; }
            public String getDateOfBirth() { return date_of_birth; }
            public String getEmergencyContact() { return emergency_contact; }
            public String getProfilePicture() { return profile_picture; }
            public String getCreatedAt() { return created_at; }
            public String getUpdatedAt() { return updated_at; }
        }
    }
}