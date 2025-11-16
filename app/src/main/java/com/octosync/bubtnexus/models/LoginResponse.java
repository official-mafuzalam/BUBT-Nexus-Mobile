package com.octosync.bubtnexus.models;

public class LoginResponse {
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
