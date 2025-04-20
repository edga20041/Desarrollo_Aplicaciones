package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.api.model.PasswordResetRequest;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@AndroidEntryPoint
public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    @Inject
    ApiService apiService;

    private String email;
    private String token; // Nuevo campo para el token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Log.d(TAG, "⭐ onCreate: ResetPasswordActivity creada.");

        email = getIntent().getStringExtra("email");
        token = getIntent().getStringExtra("token"); // Recuperar el token

        EditText newPasswordEditText = findViewById(R.id.newPasswordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa y confirma la nueva contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                return;
            }

            PasswordResetRequest request = new PasswordResetRequest();
            request.setToken(token); // Setear el token
            request.setNewPassword(newPassword);

            apiService.resetPassword(request).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    Log.d(TAG, "Respuesta del backend (restablecer contraseña): " + response.body());

                    if (response.isSuccessful() && response.body() != null && response.body().containsKey("message")) {
                        Toast.makeText(ResetPasswordActivity.this, response.body().get("message"), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "No se pudo restablecer la contraseña.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e(TAG, "Error al restablecer la contraseña: " + t.getMessage());
                    Toast.makeText(ResetPasswordActivity.this, "Error de red. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}