package com.octosync.bubtnexus.models;

public class UserResponse {
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
        private User user;

        public User getUser() {
            return user;
        }
    }
}