package com.example.desarrollo_aplicaciones.api.model;

import com.example.desarrollo_aplicaciones.entity.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApi {
    @POST("/auth/register")
    Call<String> register(@Body RegisterRequest registerRequest);
    @POST("/auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);
    @GET("/auth/user/me")
    Call<UserResponse> getUserInfo(@Header("Authorization") String authorization);
    @GET("/auth/users/{userId}")
    Call<User> getUserById(@Path("userId") int userId);
    @POST("/auth/recover")
    Call<Void> recoverPassword(@Body RecoverPasswordRequest request);
}