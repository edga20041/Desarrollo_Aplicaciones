package com.example.desarrollo_aplicaciones.LogReg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desarrollo_aplicaciones.R;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        mAuth = FirebaseAuth.getInstance();

        EditText emailInput = findViewById(R.id.emailInput);
        Button recoverPasswordButton = findViewById(R.id.recoverPasswordButton);
        TextView loginRedirect = findViewById(R.id.loginRedirect);

        recoverPasswordButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(RecoverPasswordActivity.this, "Por favor, ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecoverPasswordActivity.this, "Correo de recuperación enviado.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RecoverPasswordActivity.this, "Error al enviar correo de recuperación.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RecoverPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }
}
