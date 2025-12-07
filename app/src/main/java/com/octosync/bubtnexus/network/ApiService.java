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

    @GET("me")
    Call<UserResponse> getUser(@Header("Authorization") String token);

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String token);

    @GET("notices")
    Call<NoticesResponse> getNotices(@Header("Authorization") String token);

    @POST("routine")
    @FormUrlEncoded
    Call<RoutineResponse> getRoutine(
            @Header("Authorization") String token,
            @Field("program") String program,
            @Field("intake") String intake,
            @Field("section") String section
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

    @POST("/api/rides/create")
    Call<RideCreateResponse> createRide(
            @Header("Authorization") String token,
            @Body RideCreateRequest rideCreateRequest
    );

    @PUT("/api/rides/{rideId}/request/{requestId}")
    Call<PassengerRequestResponse> updateRequestStatus(
            @Header("Authorization") String token,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            @Body UpdatePassengerRequestActionRequest request
    );

    @PUT("/api/rides/{rideId}/status")
    Call<UpdateRideStatusResponse> updateRideStatus(
            @Header("Authorization") String token,
            @Path("rideId") int rideId,
            @Body UpdateRideStatusRequest request
    );
}