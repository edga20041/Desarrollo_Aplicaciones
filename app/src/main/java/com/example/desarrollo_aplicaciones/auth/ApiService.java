package com.example.desarrollo_aplicaciones.auth;
import com.example.desarrollo_aplicaciones.DI.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface ApiService {

    @POST("register")
    Call<Void> register(@Body User user);

    @POST("login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("reset-password")
    Call<Void> resetPassword(@Body EmailRequest email);

    @GET("user/{id}")
    Call<User> getUserById(@Path("id") int userId);

}
