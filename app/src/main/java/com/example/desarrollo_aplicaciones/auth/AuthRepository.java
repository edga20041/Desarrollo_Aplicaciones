package com.example.desarrollo_aplicaciones.auth;

import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class AuthRepository {

    private AuthService authService;

    @Inject
    public AuthRepository(AuthService authService) {
        this.authService = authService;
    }

    public Task<AuthResult> registerUser(String email, String password, String nombre, String apellido, String dni, String phone) {
        return authService.register(email, password, nombre, apellido, dni, phone);
    }

    public void saveUserData(String userId, String nombre, String apellido, String dni, String email, String phone) {
        authService.saveUserData(userId, nombre, apellido, dni, email, phone);
    }

    public void sendVerificationEmail() {
        authService.sendEmailVerification();
    }

    public void recoverPassword(String email) {
        authService.resetPassword(email);
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return authService.login(email, password);
    }
}
