// models/DeviceRegistrationRequest.java
package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class DeviceRegistrationRequest {
    @SerializedName("device_token")
    private String deviceToken;

    @SerializedName("device_type")
    private String deviceType;

    @SerializedName("device_id")
    private String deviceId;

    // Constructors
    public DeviceRegistrationRequest() {}

    public DeviceRegistrationRequest(String deviceToken) {
        this.deviceToken = deviceToken;
        this.deviceType = "android";
    }

    // Getters and setters
    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
}