package com.example.desarrollo_aplicaciones.LogReg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.MyApplication;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.auth.AuthRepository;
import com.example.desarrollo_aplicaciones.auth.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import javax.inject.Inject;

public class RegisterActivity extends AppCompatActivity {

    @Inject
    AuthRepository authRepository;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ((MyApplication) getApplication()).getAppComponent().inject(this);

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
        Button backButton = findViewById(R.id.backButton); // Mover la referencia aquí

        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        showPasswordIcon.setOnClickListener(v -> {
            if (passwordInput.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordIcon.setImageResource(R.drawable.ic_eye);
            } else {
                passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordIcon.setImageResource(R.drawable.ic_eye_off);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });

        showConfirmPasswordIcon.setOnClickListener(v -> {
            if (confirmPasswordInput.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showConfirmPasswordIcon.setImageResource(R.drawable.ic_eye);  // Mostrar ícono de "ojo abierto"
            } else {
                confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showConfirmPasswordIcon.setImageResource(R.drawable.ic_eye_off);  // Mostrar ícono de "ojo cerrado"
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
        });


        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        registerButton.setOnClickListener(v -> {
            String nombre = nombreInput.getText().toString().trim();
            String apellido = apellidoInput.getText().toString().trim();
            String dni = dniInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || phone.isEmpty() ||
                    email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registro del usuario y guardado de los datos en Firestore
            authRepository.registerUser(email, password, nombre, apellido, dni, phone).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                            if (emailTask.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this,
                                        "Código enviado a tu correo. Verifica para continuar.",
                                        Toast.LENGTH_LONG).show();

                                String codigo = generarCodigoVerificacion();
                                almacenarCodigoVerificacion(email,codigo);
                                enviarCorreo(email,codigo);

                                // Guardamos temporalmente los datos del usuario hasta la verificación
                                Intent intent = new Intent(RegisterActivity.this, VerifyCodeActivity.class);
                                intent.putExtra("userId", user.getUid());
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("apellido", apellido);
                                intent.putExtra("dni", dni);
                                intent.putExtra("email", email);
                                intent.putExtra("codigoVerificacion", codigo);
                                intent.putExtra("phone", phone);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Error al enviar código de verificación",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            });
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
                        "fecha_expiracion", fechaExpiracion // Agregamos la fecha de expiración
                ))
                .addOnSuccessListener(aVoid -> {
                    // Código almacenado con éxito
                })
                .addOnFailureListener(e -> {
                    // Manejar error
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