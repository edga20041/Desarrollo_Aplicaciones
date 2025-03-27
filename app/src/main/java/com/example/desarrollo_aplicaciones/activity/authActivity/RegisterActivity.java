package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.repository.auth.AuthServiceCallback;
import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.repository.auth.AuthRetrofitRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {

    @Inject
    AuthRetrofitRepository authRetrofitRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        EditText nombreInput = findViewById(R.id.nombreInput);
        EditText apellidoInput = findViewById(R.id.apellidoInput);
        EditText dniInput = findViewById(R.id.dniInput);
        EditText phoneInput = findViewById(R.id.phoneInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button registerButton = findViewById(R.id.registerUserButton);
        ImageView showPasswordIcon = findViewById(R.id.showPasswordIcon);
        ImageView showConfirmPasswordIcon = findViewById(R.id.showConfirmPasswordIcon);
        Button backButton = findViewById(R.id.backButton);


        configurePasswordVisibility(passwordInput, showPasswordIcon);
        configurePasswordVisibility(confirmPasswordInput, showConfirmPasswordIcon);


        backButton.setOnClickListener(v -> onBackPressed());


        registerButton.setOnClickListener(v -> {
            String nombre = nombreInput.getText().toString().trim();
            String apellido = apellidoInput.getText().toString().trim();
            String dni = dniInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (validateFields(nombre, apellido, dni, phone, email, password, confirmPassword)) {

                User user = new User(nombre, apellido, dni, email, phone);
                registerUser(user);
            }
        });
    }


    private void configurePasswordVisibility(EditText passwordField, ImageView toggleIcon) {
        passwordField.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        toggleIcon.setOnClickListener(v -> {
            if (passwordField.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordField.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye);
            } else {
                passwordField.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_off);
            }
            passwordField.setSelection(passwordField.getText().length());
        });
    }


    private boolean validateFields(String nombre, String apellido, String dni, String phone, String email, String password, String confirmPassword) {
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || phone.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void registerUser(User user) {
        authRetrofitRepository.register(user, new AuthServiceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(RegisterActivity.this, "Registro exitoso. Verifica tu correo.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, VerifyCodeActivity.class));
                finish();
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(RegisterActivity.this, "Error al registrar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}