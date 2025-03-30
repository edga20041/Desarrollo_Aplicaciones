package com.example.desarrollo_aplicaciones.repository.auth;

import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.api.model.AuthResponse;
import com.example.desarrollo_aplicaciones.api.model.LoginRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRetrofitRepository implements AuthRepository {

    private final FirebaseAuth firebaseAuth;

    @Inject
    public AuthRetrofitRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void register(User user, String password, AuthServiceCallback<Void> callback) { // Recibe la contraseña
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password) // Usa la contraseña
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null); // Registro exitoso
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    @Override
    public void login(LoginRequest loginRequest, AuthServiceCallback<AuthResponse> callback) {
        firebaseAuth.signInWithEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            AuthResponse authResponse = new AuthResponse();
                            authResponse.setUid(firebaseUser.getUid());
                            authResponse.setEmail(firebaseUser.getEmail());
                            callback.onSuccess(authResponse);
                        } else {
                            callback.onError(new Exception("Usuario no encontrado"));
                        }
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    @Override
    public void getUserById(int userId, AuthServiceCallback<User> callback) {
        callback.onError(new UnsupportedOperationException("getUserById no soportado con Firebase Authentication"));
    }
}