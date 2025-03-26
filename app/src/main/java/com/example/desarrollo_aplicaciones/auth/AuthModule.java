package com.example.desarrollo_aplicaciones.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.desarrollo_aplicaciones.auth.AuthService;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class AuthModule {

    @Provides
    @Singleton  // Asegura que solo haya una instancia de FirebaseAuth a lo largo de la aplicación
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton  // Asegura que solo haya una instancia de FirebaseFirestore a lo largo de la aplicación
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton  // Asegura que solo haya una instancia de AuthService a lo largo de la aplicación
    public AuthService provideAuthService(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        return new AuthService(firebaseAuth, firebaseFirestore);
    }
}
