package com.example.desarrollo_aplicaciones.repository.auth;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenRepository {

    private final SharedPreferences sharedPreferences;
    private static final String PREF_FILE_NAME = "secure_token_prefs";
    private static final String KEY_JWT_TOKEN = "auth_token";

    @Inject
    public TokenRepository(Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        this.sharedPreferences = EncryptedSharedPreferences.create(
                PREF_FILE_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null);
    }

    public void deleteToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_JWT_TOKEN);
        editor.apply();
    }
}