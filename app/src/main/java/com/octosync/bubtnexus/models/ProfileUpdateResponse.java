package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class ProfileUpdateResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserData data;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public UserData getData() { return data; }
    public void setData(UserData data) { this.data = data; }

    public static class UserData {
        @SerializedName("user")
        private LoginResponse.User user;

        public LoginResponse.User getUser() { return user; }
        public void setUser(LoginResponse.User user) { this.user = user; }
    }
}