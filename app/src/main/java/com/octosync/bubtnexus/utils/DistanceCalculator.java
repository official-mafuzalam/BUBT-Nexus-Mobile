// utils/DistanceCalculator.java
package com.octosync.bubtnexus.utils;

public class DistanceCalculator {

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        double R = 6371; // Earth's radius in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }

    public static String formatDistance(double distanceInKm) {
        if (distanceInKm < 1) {
            int meters = (int) (distanceInKm * 1000);
            return meters + " m";
        } else {
            return String.format("%.1f km", distanceInKm);
        }
    }
}