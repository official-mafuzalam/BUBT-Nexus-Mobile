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

    // New Ride APIs
    @POST("rides/create")
    Call<RideResponse> createRide(
            @Header("Authorization") String token,
            @Body CreateRideRequest rideRequest
    );

    @GET("rides/nearby")
    Call<RidesResponse> getNearbyRides(
            @Header("Authorization") String token,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") Integer radius,
            @Query("max_seats") Integer maxSeats,
            @Query("max_fare") Double maxFare
    );

    @GET("rides/{id}")
    Call<RideResponse> getRideDetails(
            @Header("Authorization") String token,
            @Path("id") int rideId
    );

    @POST("rides/{id}/request")
    Call<ApiResponse> requestRide(
            @Header("Authorization") String token,
            @Path("id") int rideId,
            @Body RideRequestRequest request
    );

    @PUT("rides/{rideId}/request/{requestId}")
    Call<ApiResponse> handleRideRequest(
            @Header("Authorization") String token,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            @Body ActionRequest action
    );

    @PUT("rides/{id}/status")
    Call<RideResponse> updateRideStatus(
            @Header("Authorization") String token,
            @Path("id") int rideId,
            @Body StatusUpdateRequest status
    );

    @POST("rides/{id}/location")
    Call<ApiResponse> updateLocation(
            @Header("Authorization") String token,
            @Path("id") int rideId,
            @Body LocationUpdateRequest location
    );

    @GET("rides/{id}/locations")
    Call<ApiResponse> getRideLocations(
            @Header("Authorization") String token,
            @Path("id") int rideId
    );

    @POST("rides/{id}/message")
    Call<ApiResponse> sendMessage(
            @Header("Authorization") String token,
            @Path("id") int rideId,
            @Body MessageRequest message
    );

    @GET("rides/{id}/messages")
    Call<ApiResponse> getRideMessages(
            @Header("Authorization") String token,
            @Path("id") int rideId,
            @Query("page") Integer page
    );

    @GET("rides/my-rides")
    Call<MyRidesResponse> getMyRides(
            @Header("Authorization") String token
    );

    // Device registration for push notifications
    @POST("device/register")
    Call<ApiResponse> registerDevice(
            @Header("Authorization") String token,
            @Body DeviceRegistrationRequest deviceRequest
    );

    @DELETE("device/unregister/{token}")
    Call<ApiResponse> unregisterDevice(
            @Header("Authorization") String authToken,
            @Path("token") String deviceToken
    );
}