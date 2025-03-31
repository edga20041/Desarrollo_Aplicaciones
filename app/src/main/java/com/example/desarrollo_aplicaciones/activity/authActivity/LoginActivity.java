package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.repository.auth.AuthServiceCallback;
import com.example.desarrollo_aplicaciones.api.model.LoginRequest;
import com.example.desarrollo_aplicaciones.api.model.AuthResponse;
import com.example.desarrollo_aplicaciones.repository.auth.AuthRetrofitRepository;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import com.example.desarrollo_aplicaciones.HomeActivity;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    AuthRetrofitRepository authRetrofitRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Añadir al inicio del método onCreate (después de setContentView)
        SharedPreferences preferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String savedToken = preferences.getString("auth_token", null);
        if (savedToken != null) {
            // El usuario ya tiene un token guardado, ir directamente a HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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

            // Añadir antes de authRetrofitRepository.login
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Iniciando sesión...");
            progressDialog.show();

            authRetrofitRepository.login(loginRequest, new AuthServiceCallback<AuthResponse>() {
                @Override
                public void onSuccess(AuthResponse result) {
                    // Implementar en el método onSuccess
                    if (result != null && result.getToken() != null) {
                        // Guardar token en SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("auth_token", result.getToken());
                        editor.apply();
                    }

                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);

                    // No es necesario llamar a finish() cuando usas esas flags
                    progressDialog.dismiss();
                }

                @Override
                public void onError(Throwable error) {
                    // Mantener el código original de error
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        });
    }
}