package com.example.desarrollo_aplicaciones.api.model;
import com.example.desarrollo_aplicaciones.entity.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface AuthApiService {

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("auth/user/{id}")
    Call<User> getUserById(@Path("id") int id);

}
