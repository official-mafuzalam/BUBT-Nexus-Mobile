package com.octosync.bubtnexus.network;

import com.octosync.bubtnexus.models.LoginRequest;
import com.octosync.bubtnexus.models.LoginResponse;
import com.octosync.bubtnexus.models.NoticesResponse;
import com.octosync.bubtnexus.models.ProfileUpdateRequest;
import com.octosync.bubtnexus.models.ProfileUpdateResponse;
import com.octosync.bubtnexus.models.RegisterRequest;
import com.octosync.bubtnexus.models.RoutineResponse;
import com.octosync.bubtnexus.models.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    // Profile update with multipart form data (for image upload)
    @Multipart
    @PUT("profile-update")
    Call<ProfileUpdateResponse> updateProfile(
            @Header("Authorization") String token,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("student_id") RequestBody studentId,
            @Part("faculty_id") RequestBody facultyId,
            @Part("program") RequestBody program,
            @Part("semester") RequestBody semester,
            @Part("intake") RequestBody intake,
            @Part("cgpa") RequestBody cgpa,
            @Part("department") RequestBody department,
            @Part("designation") RequestBody designation,
            @Part("office_room") RequestBody officeRoom,
            @Part("office_hours") RequestBody officeHours,
            @Part("phone") RequestBody phone,
            @Part("address") RequestBody address,
            @Part("date_of_birth") RequestBody dateOfBirth,
            @Part("emergency_contact") RequestBody emergencyContact,
            @Part MultipartBody.Part profilePicture
    );

    // Alternative: Profile update without image
    @PUT("profile-update")
    Call<ProfileUpdateResponse> updateProfileWithoutImage(
            @Header("Authorization") String token,
            @Body ProfileUpdateRequest profileUpdateRequest
    );
}