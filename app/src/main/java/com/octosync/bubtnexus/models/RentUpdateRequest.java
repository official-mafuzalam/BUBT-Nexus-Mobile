package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class RentUpdateRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("rent")
    private Integer rent;

    @SerializedName("location")
    private String location;

    @SerializedName("address_detail")
    private String addressDetail;

    @SerializedName("bedrooms")
    private Integer bedrooms;

    @SerializedName("washrooms")
    private Integer washrooms;

    @SerializedName("available_from")
    private String availableFrom;

    @SerializedName("is_available")
    private Boolean isAvailable;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public Integer getRent() { return rent; }
    public void setRent(Integer rent) { this.rent = rent; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }

    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }

    public Integer getWashrooms() { return washrooms; }
    public void setWashrooms(Integer washrooms) { this.washrooms = washrooms; }

    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
