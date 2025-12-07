package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NominatimResponse {
    @SerializedName("display_name")
    private String displayName;

    @SerializedName("address")
    private Address address;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lon")
    private String lon;

    // Getters
    public String getDisplayName() { return displayName; }
    public Address getAddress() { return address; }
    public String getLat() { return lat; }
    public String getLon() { return lon; }

    public static class Address {
        @SerializedName("road")
        private String road;

        @SerializedName("suburb")
        private String suburb;

        @SerializedName("city")
        private String city;

        @SerializedName("state")
        private String state;

        @SerializedName("country")
        private String country;

        // Getters
        public String getRoad() { return road; }
        public String getSuburb() { return suburb; }
        public String getCity() { return city; }
        public String getState() { return state; }
        public String getCountry() { return country; }

        // Helper method to get formatted address
        public String getFormattedAddress() {
            StringBuilder sb = new StringBuilder();
            if (road != null && !road.isEmpty()) {
                sb.append(road).append(", ");
            }
            if (suburb != null && !suburb.isEmpty()) {
                sb.append(suburb).append(", ");
            }
            if (city != null && !city.isEmpty()) {
                sb.append(city);
            } else if (state != null && !state.isEmpty()) {
                sb.append(state);
            }
            if (country != null && !country.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(country);
            }
            return sb.toString();
        }
    }
}