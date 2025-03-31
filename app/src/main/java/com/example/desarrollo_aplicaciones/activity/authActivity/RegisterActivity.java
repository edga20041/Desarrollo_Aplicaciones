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
import com.example.desarrollo_aplicaciones.api.model.RegisterRequest;
import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.repository.auth.AuthServiceCallback;
import com.example.desarrollo_aplicaciones.repository.auth.AuthRetrofitRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {

    @Inject
    FirebaseAuth mAuth;
    @Inject
    AuthRetrofitRepository authRetrofitRepository;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                RegisterRequest registerRequest = new RegisterRequest(nombre, apellido, dni, phone, email, password);
                registerWithFirebase(registerRequest);
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
        })
    ;}


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

    private void registerWithFirebase(RegisterRequest registerRequest) {
        authRetrofitRepository.register(registerRequest, new AuthServiceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {


                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Código enviado a tu correo. Verifica para continuar.", Toast.LENGTH_LONG).show();

                            String codigo = generarCodigoVerificacion();
                            almacenarCodigoVerificacion(registerRequest.getEmail(), codigo);
                            enviarCorreo(registerRequest.getEmail(), codigo);

                            User user = new User(registerRequest.getNombre(), registerRequest.getApellido(), registerRequest.getDni(), registerRequest.getEmail(), registerRequest.getPhone());
                            guardarDatosUsuario(user);

                            Intent intent = new Intent(RegisterActivity.this, VerifyCodeActivity.class);
                            intent.putExtra("email", registerRequest.getEmail());
                            intent.putExtra("codigoVerificacion", codigo);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al enviar código de verificación", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(RegisterActivity.this, "Error al registrar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatosUsuario(User user) {
        db.collection("users").document(user.getEmail())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RegisterActivity", "Datos de usuario guardados");
                })
                .addOnFailureListener(e -> {
                    Log.e("RegisterActivity", "Error al guardar datos de usuario", e);

                });
    }

    private String generarCodigoVerificacion() {
        String caracteres = "0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }

    private void almacenarCodigoVerificacion(String email, String codigo) {
        long fechaExpiracion = System.currentTimeMillis() + 300000; // 5 minutos de expiración
        db.collection("codigos_verificacion").document(email)
                .set(java.util.Map.of(
                        "codigo", codigo,
                        "fecha_expiracion", fechaExpiracion
                ))
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private void enviarCorreo(String email, String codigo) {
        new Thread(() -> {
            String asunto = "Tu código de verificación";
            String mensaje = "Tu código de verificación es: " + codigo;

            String usuarioCorreo = "edgardo20041@gmail.com";
            String contrasenaCorreo = "rcih ashw ikid soip";

            boolean enviado = EmailService.enviarCorreo(email, asunto, mensaje, usuarioCorreo, contrasenaCorreo);

            runOnUiThread(() -> {
                if (enviado) {
                    Toast.makeText(this, "Código enviado a tu correo.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al enviar el correo.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}