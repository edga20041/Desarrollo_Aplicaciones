package com.example.desarrollo_aplicaciones.di;

import android.content.Context;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.inject.Singleton;

import android.content.Context;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class TokenModule {

    @Provides
    @Singleton
    public TokenRepository provideTokenRepository(@ApplicationContext Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            EncryptedSharedPreferences.create(
                    context,
                    "encrypted_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return new TokenRepository(context);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Error initializing TokenRepository", e);
        }
    }
}