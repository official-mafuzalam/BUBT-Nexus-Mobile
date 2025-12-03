package com.octosync.bubtnexus.models;

public class MessageRequest {
    private String message;
    private String type;

    // Constructors
    public MessageRequest() {
        this.type = "text";
    }

    public MessageRequest(String message) {
        this.message = message;
        this.type = "text";
    }

    public MessageRequest(String message, String type) {
        this.message = message;
        this.type = type;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}