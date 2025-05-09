package com.example.desarrollo_aplicaciones.activity.authActivity;

import static com.example.desarrollo_aplicaciones.helpers.Formats.*;

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

import com.example.desarrollo_aplicaciones.helpers.Validations;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private final Validations validations = new Validations(TAG);

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
        Button backButton = findViewById(R.id.backButton);



        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        registerButton.setOnClickListener(v -> {
            String nombre = capitalizeInput(nombreInput);
            String apellido = capitalizeInput(apellidoInput);
            String dni = getTextInput(dniInput);
            String phone = getTextInput(phoneInput);
            String email = getTextInput(emailInput);
            String password = getTextInput(passwordInput);
            String confirmPassword = getTextInput(confirmPasswordInput);

            if (validations.validateFields(this, nombre, apellido, dni, phone, email, password, confirmPassword)) {
                RegisterRequest registerRequest = new RegisterRequest(email, password, nombre, apellido, phone, Integer.parseInt(dni));
                Log.d(TAG, "registerButton.OnClickListener: Realizando registro con: " + registerRequest);
                Toast.makeText(RegisterActivity.this, "Aguarde un instante, estamos procesando tu registro...", Toast.LENGTH_LONG).show();


                registerWithBackend(registerRequest);
            }
        });
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
                        finish();
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
}