/*package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.R;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecoverPasswordActivity extends AppCompatActivity {
    private static final String TAG = "RecoverPasswordActivity";
    @Inject
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
        Log.d(TAG, "⭐ onCreate: La Activity ha sido creada.");
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
}*/