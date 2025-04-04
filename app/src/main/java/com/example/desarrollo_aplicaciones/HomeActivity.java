package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.time.LocalTime;
import java.time.ZoneId;



import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    TokenRepository tokenRepository;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Elementos de la UI
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        Button logoutButton = findViewById(R.id.logoutButton);
        ImageView userImageView = findViewById(R.id.userImageView);

        // Obtenemos el usuario actual desde FirebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Lógica para determinar el saludo
        ZoneId zonaHoraria = ZoneId.of("America/Argentina/Buenos_Aires");
        LocalTime horaActual = LocalTime.now( zonaHoraria);
        String saludoDinamico;


        // Evalúa el rango de horas para el saludo
        if (horaActual.isAfter(LocalTime.of(6, 0)) && horaActual.isBefore(LocalTime.of(12, 0))) {
            saludoDinamico = "¡Buenos días";
        } else if (horaActual.isAfter(LocalTime.of(12, 0)) && horaActual.isBefore(LocalTime.of(18, 0))) {
            saludoDinamico = "¡Buenas tardes";
        } else {
            saludoDinamico = "¡Buenas noches";
        }





        // Mostramos el saludo junto con el correo del usuario
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            welcomeTextView.setText(saludoDinamico + " " + userEmail+"!");
        } else {
            welcomeTextView.setText(saludoDinamico + " Usuario");
        }

        // Configuración del botón de cerrar sesión
        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Carga estática de la imagen del avatar
        userImageView.setImageResource(R.drawable.img); // Reemplaza con tu imagen en drawable
    }

}
