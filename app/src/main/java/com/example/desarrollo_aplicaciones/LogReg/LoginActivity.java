package com.example.desarrollo_aplicaciones.LogReg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.MainActivity;
import com.example.desarrollo_aplicaciones.MyApplication;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.auth.AuthRepository;
import com.example.desarrollo_aplicaciones.auth.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {

    @Inject
    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((MyApplication) getApplication()).getAppComponent().inject(this);



        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginUserButton);
        Button backButton = findViewById(R.id.backButton);  // Reference to back button

        TextView recoverPasswordLink = findViewById(R.id.recoverPasswordLink);

        recoverPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RecoverPasswordActivity.class));
        });

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.loginUser(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Debes verificar tu correo antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
