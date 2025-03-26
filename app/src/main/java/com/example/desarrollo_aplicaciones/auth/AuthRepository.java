package com.example.desarrollo_aplicaciones.auth;

import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class AuthRepository {

    private final ApiService apiService;

    @Inject
    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<Void> registerUser(User user) {
        return apiService.register(user);
    }

    public Call<AuthResponse> loginUser(LoginRequest loginRequest) {
        return apiService.login(loginRequest);
    }

    public Call<Void> resetPassword(String email) {
        return apiService.resetPassword(new EmailRequest(email));
    }

    public Call<User> getUserById(int userId) {
        return apiService.getUserById(userId);
    }
}

