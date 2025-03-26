package com.example.desarrollo_aplicaciones.Dagger;

import com.example.desarrollo_aplicaciones.auth.AuthRepository;
import com.example.desarrollo_aplicaciones.auth.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class AppModule {

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public AuthService provideAuthService(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        return new AuthService(firebaseAuth, firebaseFirestore);
    }

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(AuthService authService) {
        return new AuthRepository(authService);
    }
}
