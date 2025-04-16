package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.api.model.RegisterRequest;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: La Activity ha sido creada.");

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
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (validateFields(nombre, apellido, dni, phone, email, password, confirmPassword)) {
                RegisterRequest registerRequest = new RegisterRequest(email, password, nombre, apellido, phone, Integer.parseInt(dni));
                Log.d(TAG, "registerButton.OnClickListener: Realizando registro con: " + registerRequest.toString()); // Log de la petición
                registerWithBackend(registerRequest);
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

        if (!isValidName(nombre)) {
            Toast.makeText(this, "Nombre inválido. Debe empezar por mayúscula y no tener números.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidSurname(apellido)) {
            Toast.makeText(this, "Apellido inválido. Debe empezar por mayúscula y no tener números.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDni(dni)) {
            Toast.makeText(this, "DNI inválido. Debe tener exactamente 8 dígitos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Teléfono inválido. Debe tener exactamente 10 dígitos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Correo electrónico inválido.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Contraseña inválida. Debe tener una mayúscula, números y mínimo 9 caracteres.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void registerWithBackend(RegisterRequest registerRequest) {
        Call<ResponseBody> call = apiService.register(registerRequest);
        Log.d(TAG, "registerWithBackend: Llamando al endpoint de registro...");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String mensaje = response.body().string(); // Lee la respuesta como texto plano
                        Log.d(TAG, "Registro exitoso: " + mensaje);
                        Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, VerifyCodeActivity.class);
                        intent.putExtra("email", registerRequest.getEmail());
                        startActivity(intent);
                    } catch (IOException e) {
                        Log.e(TAG, "Error al leer la respuesta del servidor", e);
                    }
                } else if (response.code() == 400) {
                    Log.e(TAG, "Usuario ya registrado.");
                    Toast.makeText(RegisterActivity.this, "El usuario ya está registrado.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error inesperado del servidor.");
                    Toast.makeText(RegisterActivity.this, "Error en el registro. Por favor, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Fallo en la llamada al backend", t);
                Toast.makeText(RegisterActivity.this, "Error de conexión. Verifica tu conexión a internet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public String toString() {
        return "RegisterActivity{" +
                "authApi=" + apiService +
                ", tokenRepository=" + tokenRepository +
                '}';
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Z][a-zA-Z]*$");
    }

    private boolean isValidSurname(String surname) {
        return surname.matches("^[A-Z][a-zA-Z]*$");
    }

    private boolean isValidDni(String dni) {
        return dni.matches("^\\d{8}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d).{9,}$");
    }

}