package com.octosync.bubtnexus.network;

import com.octosync.bubtnexus.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
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

    @PUT("profile-update")
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

    // Get all programs
    @GET("programs")
    Call<ProgramsResponse> getPrograms();

    // Get semester options
    @GET("semesters")
    Call<SemesterOptionsResponse> getSemesterOptions();

    // Get department options
    @GET("departments")
    Call<ListResponse> getDepartments();

    // Get designation options
    @GET("designations")
    Call<ListResponse> getDesignations();

    // ===== RENT API ENDPOINTS =====

    // List all rents (with optional filters)
    @GET("/api/rents")
    Call<RentsResponse> getRents(
            @Query("category") String category,
            @Query("location") String location,
            @Query("available") Boolean available,
            @Query("min_rent") Integer minRent,
            @Query("max_rent") Integer maxRent
    );

    // View single rent post
    @GET("/api/rents/{id}")
    Call<RentResponse> getRent(@Path("id") int id);

    // Search rents
    @GET("/api/rents/search")
    Call<RentsResponse> searchRents(
            @Query("q") String query,
            @Query("category") String category,
            @Query("location") String location,
            @Query("available") Boolean available
    );

    // Create rent post
    @POST("/api/rents")
    Call<RentCreateResponse> createRent(
            @Header("Authorization") String token,
            @Body RentCreateRequest rentCreateRequest
    );

    // Update rent post
    @PUT("/api/rents/{id}")
    Call<RentUpdateResponse> updateRent(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body RentUpdateRequest updateRentRequest
    );

    // Delete rent post
    @DELETE("/api/rents/{id}")
    Call<RentDeleteResponse> deleteRent(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    // Set availability
    @PUT("/api/rents/{id}/availability")
    Call<RentSetAvailabilityResponse> setAvailability(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body RentSetAvailabilityRequest setAvailabilityRequest
    );

    // Get my rent posts
    @GET("/api/my-rents")
    Call<List<Rent>> getMyRents(@Header("Authorization") String token);
}