package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentCreateRequest {
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

    // Constructor
    public RentCreateRequest(String title, String description, String category, String contactNumber,
                             int rent, String location, String addressDetail, int bedrooms,
                             int washrooms, String availableFrom) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.contactNumber = contactNumber;
        this.rent = rent;
        this.location = location;
        this.addressDetail = addressDetail;
        this.bedrooms = bedrooms;
        this.washrooms = washrooms;
        this.availableFrom = availableFrom;
    }

    // Getters and Setters
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
}