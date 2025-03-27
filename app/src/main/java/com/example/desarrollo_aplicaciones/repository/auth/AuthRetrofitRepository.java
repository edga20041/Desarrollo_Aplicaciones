package com.example.desarrollo_aplicaciones.repository.auth;

import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.api.model.AuthResponse;
import com.example.desarrollo_aplicaciones.api.model.LoginRequest;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
//import com.example.desarrollo_aplicaciones.data.api.model.EmailRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRetrofitRepository implements AuthRepository {

    private final ApiService apiService;

    @Inject
    public AuthRetrofitRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void register(User user, AuthServiceCallback<Void> callback) {
        apiService.register(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null); // Registro exitoso
                } else {
                    callback.onError(new Exception("Error al registrar usuario"));
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
                    callback.onError(new Exception("Error al iniciar sesión"));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /*@Override
    public void resetPassword(EmailRequest emailRequest, AuthServiceCallback<Void> callback) {
        apiService.resetPassword(emailRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null); // Reseteo exitoso
                } else {
                    callback.onError(new Exception("Error al resetear contraseña"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t);
            }
        });
    }*/

    @Override
    public void getUserById(int userId, AuthServiceCallback<User> callback) {
        apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error al obtener usuario"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}