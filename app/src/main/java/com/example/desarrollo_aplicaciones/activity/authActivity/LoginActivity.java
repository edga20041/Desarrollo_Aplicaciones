package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.AuthApi; // Asegúrate de importar AuthApi
import com.example.desarrollo_aplicaciones.api.model.AuthResponse;
import com.example.desarrollo_aplicaciones.api.model.LoginRequest;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import com.example.desarrollo_aplicaciones.HomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @Inject
    AuthApi authApi;

    @Inject
    TokenRepository tokenRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: La Activity ha sido creada.");

        String savedToken = tokenRepository.getToken();
        Log.d(TAG, "Token recuperado al inicio: " + savedToken);
        if (savedToken != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginUserButton);
        TextView recoverPasswordLink = findViewById(R.id.recoverPasswordLink);
        TextView registerLink = findViewById(R.id.registerLink);

        recoverPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RecoverPasswordActivity.class));
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(email, password);

            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Iniciando sesión...");
            progressDialog.show();

            Call<AuthResponse> call = authApi.login(loginRequest);
            call.enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        AuthResponse authResponse = response.body();
                        if (authResponse != null && authResponse.getToken() != null) {
                            tokenRepository.saveToken(authResponse.getToken());
                            Log.d(TAG, "Token guardado: " + authResponse.getToken());
                            Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Log.e(TAG, "Error: Respuesta de inicio de sesión nula o sin token.");
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Error en la respuesta de inicio de sesión. Código: " + response.code());
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Credenciales incorrectas.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Error de conexión al iniciar sesión: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Error de conexión al iniciar sesión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: La Activity está a punto de hacerse visible.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: La Activity es visible y tiene el foco.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: La Activity está perdiendo el foco.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: La Activity ya no es visible.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: La Activity está volviendo a empezar después de detenerse.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: La Activity está siendo destruida.");
    }
}