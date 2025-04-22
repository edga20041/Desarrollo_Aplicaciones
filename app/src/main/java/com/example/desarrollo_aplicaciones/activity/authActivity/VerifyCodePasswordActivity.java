package com.example.desarrollo_aplicaciones.activity.authActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class VerifyCodePasswordActivity extends AppCompatActivity {

    private static final String TAG = "VerifyCodePasswordActivity";

    @Inject
    ApiService apiService;

    private String email;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_password);
        Log.d(TAG, "⭐ onCreate: VerifyCodeActivity creada.");

        email = getIntent().getStringExtra("email");

        EditText codeEditText = findViewById(R.id.codeEditText);
        Button verifyCodeButton = findViewById(R.id.verifyCodeButton);
        TextView resendCodeTextView = findViewById(R.id.resendCodeTextView);

        verifyCodeButton.setOnClickListener(v -> {
            String code = codeEditText.getText().toString().trim();

            if (code.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa el código.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("code", code);

            apiService.validateRecoveryCode(requestBody).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Log.d(TAG, "Respuesta del backend (validar código): " + response.body());
                    if (response.isSuccessful() && response.body() != null && response.body().containsKey("message") && response.body().get("message").equals("Código válido") && response.body().containsKey("token")) {
                        String token = (String) response.body().get("token");
                        Toast.makeText(VerifyCodePasswordActivity.this, "Código verificado.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyCodePasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("token", token); // Pasar el token
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerifyCodePasswordActivity.this, "Código inválido. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e(TAG, "Error al validar el código: " + t.getMessage());
                    Toast.makeText(VerifyCodePasswordActivity.this, "Error de red. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        resendCodeTextView.setOnClickListener(v -> {
            Toast.makeText(VerifyCodePasswordActivity.this, "Enviando codigo nuevo a tu correo...", Toast.LENGTH_LONG).show();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);

            apiService.resendRecoveryCodePassword(requestBody).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().containsKey("message")) {
                        Toast.makeText(VerifyCodePasswordActivity.this, response.body().get("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VerifyCodePasswordActivity.this, "No se pudo reenviar el código.", Toast.LENGTH_SHORT).show();
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e(TAG, "Error al reenviar el código: " + t.getMessage());
                    Toast.makeText(VerifyCodePasswordActivity.this, "Error de red. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

