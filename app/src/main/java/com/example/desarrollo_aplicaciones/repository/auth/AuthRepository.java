package com.example.desarrollo_aplicaciones.repository.auth;


import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.api.model.AuthResponse;
import com.example.desarrollo_aplicaciones.api.model.LoginRequest;
//import com.example.desarrollo_aplicaciones.data.api.model.EmailRequest;

public interface AuthRepository {
    void register(User user, AuthServiceCallback<Void> callback);

    void login(LoginRequest loginRequest, AuthServiceCallback<AuthResponse> callback);

    // Callback para el resultado de resetear contraseña
   // void resetPassword(EmailRequest emailRequest, AuthServiceCallback<Void> callback);


    void getUserById(int userId, AuthServiceCallback<User> callback);

}