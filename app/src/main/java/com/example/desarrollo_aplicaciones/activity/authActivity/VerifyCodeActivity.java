package com.example.desarrollo_aplicaciones.activity.authActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.MainActivity;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.repository.auth.AuthRetrofitRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VerifyCodeActivity extends AppCompatActivity {

    @Inject
    AuthRetrofitRepository authRetrofitRepository;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email, codigoVerificacionGenerado;
    private EditText codigoIngresadoEditText;
    private Button verificarCodigoButton;
    private Button btnReenviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        email = getIntent().getStringExtra("email");
        codigoVerificacionGenerado = getIntent().getStringExtra("codigoVerificacion");

        codigoIngresadoEditText = findViewById(R.id.codigoIngresadoEditText);
        verificarCodigoButton = findViewById(R.id.verifyCodeButton);
        btnReenviar = findViewById(R.id.btnReenviar);

        verificarCodigoButton.setOnClickListener(v -> verificarCodigo());
        btnReenviar.setOnClickListener(v -> reenviarCodigo());
    }

    private void verificarCodigo() {
        String codigoIngresado = codigoIngresadoEditText.getText().toString();

        db.collection("codigos_verificacion").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String codigoAlmacenado = document.getString("codigo");
                            if (document.contains("fecha_expiracion")) {
                                Long fechaExpiracionLong = document.getLong("fecha_expiracion");
                                if (codigoAlmacenado != null && codigoAlmacenado.equals(codigoIngresado) && fechaExpiracionLong != null && System.currentTimeMillis() < fechaExpiracionLong) {
                                    // Código válido
                                    Toast.makeText(this, "Cuenta verificada. ¡Bienvenido!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                } else {
                                    // Código inválido o expirado
                                    Toast.makeText(this, "Código inválido o expirado.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // fecha_expiracion no existe
                                Toast.makeText(this, "Código expirado.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Código no encontrado
                            Toast.makeText(this, "Código no encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Manejar error
                        Toast.makeText(this, "Error al verificar el código.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void reenviarCodigo() {
        String nuevoCodigo = generarCodigoVerificacion();
        almacenarCodigoVerificacion(email, nuevoCodigo);
        enviarCorreo(email, nuevoCodigo);
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
                    // Código almacenado con éxito
                })
                .addOnFailureListener(e -> {
                    // Manejar error
                });
    }

    private void enviarCorreo(String email, String codigo) {
        new Thread(() -> {
            String asunto = "Tu nuevo código de verificación";
            String mensaje = "Tu nuevo código de verificación es: " + codigo;

            String usuarioCorreo = "edgardo20041@gmail.com";
            String contrasenaCorreo = "rcih ashw ikid soip";
            boolean enviado = EmailService.enviarCorreo(email, asunto, mensaje, usuarioCorreo, contrasenaCorreo);

            runOnUiThread(() -> {
                if (enviado) {
                    Toast.makeText(this, "Nuevo código enviado a tu correo.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al enviar el correo.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}