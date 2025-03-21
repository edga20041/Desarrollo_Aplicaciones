package com.example.desarrollo_aplicaciones.LogReg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.desarrollo_aplicaciones.MainActivity;
import com.example.desarrollo_aplicaciones.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyCodeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button btnReenviar, verifyCodeButton;
    private TextView mensajeConfirmacion;
    private Handler handler = new Handler();
    private static final int TIEMPO_ESPERA = 30000; // 30 segundos de espera


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mensajeConfirmacion = findViewById(R.id.mensajeConfirmacion);
        btnReenviar = findViewById(R.id.btnReenviar);
        verifyCodeButton = findViewById(R.id.verifyCodeButton);

        mensajeConfirmacion.setText("📩 Revisa tu correo electrónico y confirma tu cuenta para continuar.");

        btnReenviar.setOnClickListener(v -> reenviarCodigo());

        verifyCodeButton.setOnClickListener(v -> verificarManual());


        verificarEstadoCuenta();
    }

    private void reenviarCodigo() {
        btnReenviar.setEnabled(false);
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(VerifyCodeActivity.this, "Código reenviado. Revisa tu correo.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(VerifyCodeActivity.this, "Error al reenviar el código.", Toast.LENGTH_SHORT).show();
                }

                handler.postDelayed(() -> btnReenviar.setEnabled(true), TIEMPO_ESPERA);

            });
        }
    }

    private void verificarEstadoCuenta() {
        handler.postDelayed(() -> user.reload().addOnCompleteListener(task -> {
            if (user.isEmailVerified()) {
                Toast.makeText(VerifyCodeActivity.this, "Cuenta verificada. ¡Bienvenido!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
                finish();
            } else {
                verificarEstadoCuenta();
            }
        }), 5000); // Verifica cada 5 segundos
    }

    private void verificarManual() {
        user.reload().addOnCompleteListener(task -> {
            if (user.isEmailVerified()) {
                Toast.makeText(this, "Cuenta verificada. ¡Bienvenido!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Aún no has verificado tu cuenta. Revisa tu correo.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
