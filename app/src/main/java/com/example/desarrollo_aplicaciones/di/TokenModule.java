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

@Module
@InstallIn(SingletonComponent.class)
public class TokenModule {

    @Provides
    @Singleton
    public TokenRepository provideTokenRepository(@ApplicationContext Context context) throws GeneralSecurityException, IOException {
        return new TokenRepository(context);
    }
}