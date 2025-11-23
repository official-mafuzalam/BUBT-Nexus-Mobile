package com.octosync.bubtnexus.models;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Add getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}