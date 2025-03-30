/*package com.example.desarrollo_aplicaciones.repository.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)

public class AuthModule {


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
*/ //NO ES NECESARIO