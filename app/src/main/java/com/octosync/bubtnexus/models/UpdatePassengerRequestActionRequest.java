package com.octosync.bubtnexus.models;

public class UpdatePassengerRequestActionRequest {
    private String action;

    public UpdatePassengerRequestActionRequest(String action) {
        this.action = action;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}