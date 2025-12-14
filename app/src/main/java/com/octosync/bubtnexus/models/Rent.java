package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// ===== RENT MODELS =====

public class Rent {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("rent")
    private int rent;

    @SerializedName("location")
    private String location;

    @SerializedName("address_detail")
    private String addressDetail;

    @SerializedName("bedrooms")
    private int bedrooms;

    @SerializedName("washrooms")
    private int washrooms;

    @SerializedName("available_from")
    private String availableFrom;

    @SerializedName("is_available")
    private int isAvailable;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("user")
    private User user;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getRent() { return rent; }
    public void setRent(int rent) { this.rent = rent; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getWashrooms() { return washrooms; }
    public void setWashrooms(int washrooms) { this.washrooms = washrooms; }

    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }

    public int getIsAvailable() { return isAvailable; }
    public void setIsAvailable(int isAvailable) { this.isAvailable = isAvailable; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}