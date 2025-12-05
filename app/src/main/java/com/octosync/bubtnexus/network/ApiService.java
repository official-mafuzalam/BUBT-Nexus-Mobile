package com.octosync.bubtnexus.network;

import com.octosync.bubtnexus.models.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    // Existing methods
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);

    @GET("user")
    Call<UserResponse> getUser(@Header("Authorization") String token);

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String token);

    @GET("notices")
    Call<NoticesResponse> getNotices(@Header("Authorization") String token);

    @GET("routine")
    Call<RoutineResponse> getRoutine(
            @Header("Authorization") String token,
            @Query("program") String program,
            @Query("semester") String semester,
            @Query("intake") String intake
    );

    @PUT("profile")
    Call<ProfileUpdateResponse> updateProfile(
            @Header("Authorization") String token,
            @Body ProfileUpdateRequest profileUpdateRequest
    );

    @GET("/api/rides/nearby")
    Call<NearbyRidesResponse> getNearbyRides(
            @Header("Authorization") String token,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") Integer radius,
            @Query("max_seats") Integer maxSeats,
            @Query("max_fare") Double maxFare
    );

    @GET("/api/rides/{rideId}")
    Call<RideDetailsResponse> getRideDetails(
            @Header("Authorization") String token,
            @Path("rideId") int rideId
    );

    @POST("/api/rides/{rideId}/request")
    Call<RideRequestResponse> requestRide(
            @Header("Authorization") String token,
            @Path("rideId") int rideId,
            @Body RideRequest rideRequest
    );
}