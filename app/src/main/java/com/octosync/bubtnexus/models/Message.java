package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class Message {
    private int id;

    @SerializedName("ride_id")
    private int rideId;

    @SerializedName("sender_id")
    private int senderId;

    private String message;
    private String type;

    @SerializedName("read_at")
    private String readAt;

    @SerializedName("created_at")
    private String createdAt;

    private User sender;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
}