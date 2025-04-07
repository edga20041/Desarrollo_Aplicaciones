package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.desarrollo_aplicaciones.api.model.AuthApi;
import com.example.desarrollo_aplicaciones.api.model.UserResponse;
import com.example.desarrollo_aplicaciones.entity.Delivery;
import com.example.desarrollo_aplicaciones.repository.auth.DeliveryAdapter;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    TokenRepository tokenRepository;
    @Inject
    AuthApi authApi; // Inyecta AuthApi

    private RecyclerView deliveriesRecyclerView;
    private DeliveryAdapter deliveryAdapter;
    private List<Delivery> deliveryList;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Elementos de la UI
        welcomeTextView = findViewById(R.id.welcomeTextView);
        Button logoutButton = findViewById(R.id.logoutButton);
        ImageView userImageView = findViewById(R.id.userImageView);
        deliveriesRecyclerView = findViewById(R.id.deliveriesRecyclerView);

        // Lógica para el saludo
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

        // Obtener información del usuario usando el token
        String token = tokenRepository.getToken();
        if (token != null && !token.isEmpty()) {
            obtenerInfoUsuario(token, saludoDinamico);
        } else {
            welcomeTextView.setText(saludoDinamico + " Usuario");
        }

        // Configuración del botón de cerrar sesión
        logoutButton.setOnClickListener(v -> {
            tokenRepository.clearToken(); // Limpiar el token al cerrar sesión
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Carga estática de la imagen del avatar
        userImageView.setImageResource(R.drawable.img);

        // Configuración del RecyclerView
        deliveriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deliveryList = new ArrayList<>();
        deliveryAdapter = new DeliveryAdapter(deliveryList);
        deliveriesRecyclerView.setAdapter(deliveryAdapter);

        // Obtener el historial de entregas (esto dependerá de cómo esté implementado en tu backend)
        // Por ahora, lo comentamos ya que probablemente necesites un endpoint específico para esto
        // obtenerHistorialEntregas();
    }

    private void obtenerInfoUsuario(String token, String saludo) {
        // Asume que tienes un endpoint en tu backend para obtener la información del usuario
        // a partir del token (por ejemplo, /api/user/me)
        Call<UserResponse> call = authApi.getUserInfo("Bearer " + token); // Incluye el token en el encabezado Authorization
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
                    // Posiblemente manejar un token inválido o expirado aquí (redirigir al login)
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("HomeActivity", "Error de conexión al obtener información del usuario: " + t.getMessage());
                welcomeTextView.setText(saludo + " Usuario");
            }
        });
    }

    // Esto dependerá de tu backend y cómo maneja el historial de entregas
    // private void obtenerHistorialEntregas() {
    //     String token = tokenRepository.getToken();
    //     if (token != null && !token.isEmpty()) {
    //         Call<List<Delivery>> call = authApi.getDeliveryHistory("Bearer " + token);
    //         call.enqueue(new Callback<List<Delivery>>() {
    //             @Override
    //             public void onResponse(Call<List<Delivery>> call, Response<List<Delivery>> response) {
    //                 if (response.isSuccessful()) {
    //                     List<Delivery> deliveries = response.body();
    //                     if (deliveries != null) {
    //                         deliveryList.clear();
    //                         deliveryList.addAll(deliveries);
    //                         deliveryAdapter.notifyDataSetChanged();
    //                     }
    //                 } else {
    //                     Log.e("HomeActivity", "Error obteniendo el historial de entregas. Código: " + response.code());
    //                 }
    //             }
    //
    //             @Override
    //             public void onFailure(Call<List<Delivery>> call, Throwable t) {
    //                 Log.e("HomeActivity", "Error de conexión al obtener el historial de entregas: " + t.getMessage());
    //             }
    //         });
    //     }
    // }
}