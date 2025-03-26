package com.example.desarrollo_aplicaciones.auth;
import com.example.desarrollo_aplicaciones.DI.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET ("user/{id}") //Endpoint: https://api.example.com/users/{id}
    Call<User> getUserById(@Path("id") int userId);
}
