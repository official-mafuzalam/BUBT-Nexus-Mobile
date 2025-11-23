package com.octosync.bubtnexus.network;

import com.octosync.bubtnexus.models.LoginRequest;
import com.octosync.bubtnexus.models.LoginResponse;
import com.octosync.bubtnexus.models.NoticesResponse;
import com.octosync.bubtnexus.models.RegisterRequest;
import com.octosync.bubtnexus.models.RoutineResponse;
import com.octosync.bubtnexus.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
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
}