package com.octosync.bubtnexus.models;

public class ActionRequest {
    private String action;

    // Constructors
    public ActionRequest() {}

    public ActionRequest(String action) {
        this.action = action;
    }

    // Getters and setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}