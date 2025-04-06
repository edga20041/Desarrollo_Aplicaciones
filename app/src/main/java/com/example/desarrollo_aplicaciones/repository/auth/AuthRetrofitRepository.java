package com.example.desarrollo_aplicaciones.repository.auth;

import com.example.desarrollo_aplicaciones.api.model.*;
import com.example.desarrollo_aplicaciones.entity.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRetrofitRepository implements AuthRepository {

    private final AuthApiService apiService;

    public AuthRetrofitRepository (AuthApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void register(RegisterRequest registerRequest, AuthServiceCallback<Void> callback) {
        apiService.register(registerRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Error al registrarse: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void login(LoginRequest loginRequest, AuthServiceCallback<AuthResponse> callback) {
        apiService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Login fallido: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void getUserById(int userId, AuthServiceCallback<User> callback) {
        apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("No se encontró el usuario: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
