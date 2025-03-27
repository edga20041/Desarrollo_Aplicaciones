/*package com.example.desarrollo_aplicaciones.LogReg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.Dagger.DaggerAppComponent;
import com.example.desarrollo_aplicaciones.R;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class RecoverPasswordActivity extends AppCompatActivity {

    @Inject
    AuthRepository authRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        ((MyApplication) getApplication()).getAppComponent().inject(this);

        EditText emailInput = findViewById(R.id.emailInput);
        Button recoverPasswordButton = findViewById(R.id.recoverPasswordButton);
        TextView loginRedirect = findViewById(R.id.loginRedirect);

        recoverPasswordButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(RecoverPasswordActivity.this, "Por favor, ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.resetPassword(email).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RecoverPasswordActivity.this, "Correo de recuperación enviado.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RecoverPasswordActivity.this, "Error al enviar el correo.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RecoverPasswordActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });



        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RecoverPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }
}
*/