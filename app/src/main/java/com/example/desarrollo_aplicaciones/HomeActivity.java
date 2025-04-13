package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.desarrollo_aplicaciones.api.model.AuthApi;
import com.example.desarrollo_aplicaciones.api.model.UserResponse;
import com.example.desarrollo_aplicaciones.fragmentHome.ListaEntregasFragment; // Importa este
import com.example.desarrollo_aplicaciones.repository.auth.HistorialEntregasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.time.LocalTime;
import java.time.ZoneId;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    TokenRepository tokenRepository;
    @Inject
    AuthApi authApi;

    private TextView welcomeTextView;
    private Button btnVerHistorial;
    private Button btnVerRutas; // Ahora actuará como "Ver Entregas"
    private FrameLayout contenedorHistorial;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        Button logoutButton = findViewById(R.id.logoutButton);
        ImageView userImageView = findViewById(R.id.userImageView);
        btnVerHistorial = findViewById(R.id.btn_ver_historial);
        btnVerRutas = findViewById(R.id.btn_ver_rutas);
        btnVerRutas.setText("Ver Entregas"); // Cambiar el texto del botón
        contenedorHistorial = findViewById(R.id.contenedorHistorial);
        fragmentContainer = findViewById(R.id.fragment_container);

        ZoneId zonaHoraria = ZoneId.of("America/Argentina/Buenos_Aires");
        LocalTime horaActual = LocalTime.now(zonaHoraria);
        String saludoDinamico;
        if (horaActual.isAfter(LocalTime.of(6, 0)) && horaActual.isBefore(LocalTime.of(12, 0))) {
            saludoDinamico = "¡Buenos días";
        } else if (horaActual.isAfter(LocalTime.of(12, 0)) && horaActual.isBefore(LocalTime.of(18, 0))) {
            saludoDinamico = "¡Buenas tardes";
        } else {
            saludoDinamico = "¡Buenas noches";
        }

        String token = tokenRepository.getToken();
        if (token != null && !token.isEmpty()) {
            obtenerInfoUsuario(token, saludoDinamico);
        } else {
            welcomeTextView.setText(saludoDinamico + " Usuario");
        }

        logoutButton.setOnClickListener(v -> {
            tokenRepository.clearToken();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        userImageView.setImageResource(R.drawable.img);

        // Listener para el botón "Ver Historial"
        btnVerHistorial.setOnClickListener(v -> {
            contenedorHistorial.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            cargarHistorialEntregasFragment();
        });

        // Listener para el botón "Ver Entregas"
        btnVerRutas.setOnClickListener(v -> {
            contenedorHistorial.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
            cargarListaEntregasFragment(); // Carga el fragmento de la lista de entregas
        });

        // Ocultar contenedor del mapa al inicio si no quieres mostrar nada por defecto
        fragmentContainer.setVisibility(View.GONE);
    }

    private void cargarHistorialEntregasFragment() {
        HistorialEntregasFragment historialFragment = new HistorialEntregasFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorHistorial, historialFragment);
        fragmentTransaction.commit();
    }

    private void cargarListaEntregasFragment() {
        ListaEntregasFragment listaEntregasFragment = new ListaEntregasFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, listaEntregasFragment);
        transaction.commit();
    }

    private void obtenerInfoUsuario(String token, String saludo) {
        Call<UserResponse> call = authApi.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    if (userResponse != null && userResponse.getName() != null) {
                        welcomeTextView.setText(saludo + " " + userResponse.getName() + "!");
                    } else {
                        welcomeTextView.setText(saludo + " Usuario");
                    }
                } else {
                    Log.e("HomeActivity", "Error obteniendo información del usuario. Código: " + response.code());
                    welcomeTextView.setText(saludo + " Usuario");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("HomeActivity", "Error de conexión al obtener información del usuario: " + t.getMessage());
                welcomeTextView.setText(saludo + " Usuario");
            }
        });
    }

    private void mostrarMapaFragment() {
        // Ya no se usa directamente aquí
    }
}