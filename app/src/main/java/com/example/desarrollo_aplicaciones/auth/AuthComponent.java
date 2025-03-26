package com.example.desarrollo_aplicaciones.auth;


import com.example.desarrollo_aplicaciones.auth.AuthRepository;

import dagger.Component;

@Component(modules = AuthModule.class)
public interface AuthComponent {
    void inject(AuthRepository authRepository);
    void inject(AuthService authService);
}
