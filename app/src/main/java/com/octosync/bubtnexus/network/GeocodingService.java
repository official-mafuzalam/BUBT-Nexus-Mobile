package com.octosync.bubtnexus.network;

import com.octosync.bubtnexus.models.NominatimResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingService {
    // Reverse geocoding (coordinates to address)
    @GET("reverse")
    Call<NominatimResponse> reverseGeocode(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("format") String format,
            @Query("zoom") int zoom,
            @Query("addressdetails") int addressDetails
    );

    // Forward geocoding (address to coordinates)
    @GET("search")
    Call<List<NominatimResponse>> forwardGeocode(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit
    );
}