package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.api.model.UserResponse;
import com.example.desarrollo_aplicaciones.fragmentHome.ListaEntregasFragment;
//import com.example.desarrollo_aplicaciones.repository.auth.HistorialEntregasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.HistorialEntregasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalTime;
import java.time.ZoneId;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    TokenRepository tokenRepository;
    @Inject
    ApiService apiService;

    private TextView welcomeTextView;
    private Button btnVerHistorial;
    private Button btnVerEntregasPendientes;
    private Button btnFinalizarRuta;
    private FrameLayout contenedorHistorial;
    private FrameLayout fragmentContainer;
    private View layoutEntregaActual;
    private TextView textViewEntregaActualClienteTexto;
    private TextView textViewEntregaActualCliente;
    private TextView textViewEntregaActualProductoTexto;
    private TextView textViewEntregaActualProducto;
    private LinearLayout contentLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "ANTES de setContentView()");
        try {
            setContentView(R.layout.activity_home);
            Log.d("HomeActivity", "DESPUÉS de setContentView()");

            welcomeTextView = findViewById(R.id.welcomeTextView);
            ImageView userImageView = findViewById(R.id.userImageView);
            btnVerHistorial = findViewById(R.id.btn_ver_historial);
            btnVerEntregasPendientes = findViewById(R.id.btn_ver_rutas);
            btnFinalizarRuta = findViewById(R.id.btnFinalizarRuta);
            contenedorHistorial = findViewById(R.id.contenedorHistorial);
            fragmentContainer = findViewById(R.id.fragment_container);
            contentLayout = findViewById(R.id.content_layout);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            layoutEntregaActual = findViewById(R.id.layoutRutaAsignada);

            contentLayout.setVisibility(View.VISIBLE);
            contenedorHistorial.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.GONE);
            if (layoutEntregaActual != null) layoutEntregaActual.setVisibility(View.GONE);
            if (btnFinalizarRuta != null) btnFinalizarRuta.setVisibility(View.GONE);

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
                Log.d("HomeActivity", "Token encontrado en onCreate: " + token);
                obtenerInfoUsuario(token, saludoDinamico);
            } else {
                Log.w("HomeActivity", "Token no encontrado en onCreate");
                welcomeTextView.setText(saludoDinamico + " Usuario!");
            }
            userImageView.setImageResource(R.drawable.img);

            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Log.d("HomeActivity", "Item seleccionado: " + item.getTitle());

                contentLayout.setVisibility(View.GONE);
                contenedorHistorial.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.GONE);
                if (layoutEntregaActual != null) layoutEntregaActual.setVisibility(View.GONE);
                if (btnFinalizarRuta != null) btnFinalizarRuta.setVisibility(View.GONE);

                if (itemId == R.id.navigation_home) {
                    Log.d("HomeActivity", "Mostrando Home");
                    contentLayout.setVisibility(View.VISIBLE);
                    return true;

                } else if (itemId == R.id.navigation_history) {
                    Log.d("HomeActivity", "Mostrando Historial");
                    contenedorHistorial.setVisibility(View.VISIBLE);
                    cargarHistorialEntregasFragment();
                    return true;

                } else if (itemId == R.id.navigation_new_orders) {
                    Log.d("HomeActivity", "Mostrando Nuevos Pedidos");
                    fragmentContainer.setVisibility(View.VISIBLE);
                    cargarListaEntregasFragment();
                    return true;

                } else if (itemId == R.id.navigation_logout) {
                    Log.d("HomeActivity", "Ejecutando Logout");
                    realizarLogout();
                    return true;
                }
                return false;
            });

            bottomNavigationView.setSelectedItemId(R.id.navigation_home);

            btnVerHistorial.setOnClickListener(v -> {
                Log.d("HomeActivity", "Botón Ver Historial presionado (REDUNDANTE)");
                bottomNavigationView.setSelectedItemId(R.id.navigation_history);
            });

            btnVerEntregasPendientes.setOnClickListener(v -> {
                Log.d("HomeActivity", "Botón Ver Entregas presionado (REDUNDANTE)");
                bottomNavigationView.setSelectedItemId(R.id.navigation_new_orders);
            });

            if (btnFinalizarRuta != null) {
                btnFinalizarRuta.setOnClickListener(v -> {
                    Log.d("HomeActivity", "Botón Finalizar Entrega/Ruta presionado");
                    Toast.makeText(this, "Funcionalidad para finalizar entrega/ruta.", Toast.LENGTH_SHORT).show();
                });
            }

        } catch (Exception e) {
            Log.e("HomeActivity", "¡ERROR FATAL EN onCreate()!: " + e.getMessage(), e);
            Toast.makeText(this, "Error al iniciar la actividad", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void cargarHistorialEntregasFragment() {
        Log.d("HomeActivity", "cargarHistorialEntregasFragment() llamado");
        Fragment historialFragment = getSupportFragmentManager().findFragmentByTag("HISTORIAL_FRAGMENT");
        if (historialFragment == null) {
            historialFragment = new HistorialEntregasFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contenedorHistorial, historialFragment, "HISTORIAL_FRAGMENT");
        fragmentTransaction.commitAllowingStateLoss();
        Log.d("HomeActivity", "Fragment de historial cargado/mostrado.");
    }

    private void cargarListaEntregasFragment() {
        Log.d("HomeActivity", "cargarListaEntregasFragment() llamado");
        Fragment listaEntregasFragment = getSupportFragmentManager().findFragmentByTag("LISTA_ENTREGAS_FRAGMENT");
        if (listaEntregasFragment == null) {
            listaEntregasFragment = new ListaEntregasFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, listaEntregasFragment, "LISTA_ENTREGAS_FRAGMENT");
        transaction.commitAllowingStateLoss();
        Log.d("HomeActivity", "Fragment de lista de entregas cargado/mostrado.");
    }

    private void realizarLogout() {
        Log.d("HomeActivity", "realizarLogout() llamado");
        tokenRepository.clearToken();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Log.d("HomeActivity", "Logout completado, redirigiendo a MainActivity.");
    }

    private void obtenerInfoUsuario(String token, String saludo) {
        Log.d("HomeActivity", "obtenerInfoUsuario() llamado con token: " + token);
        Call<UserResponse> call = apiService.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                Log.d("HomeActivity", "Respuesta de obtenerInfoUsuario(): Código " + response.code());
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    if (userResponse != null && userResponse.getName() != null) {
                        Log.d("HomeActivity", "Información del usuario recibida: " + userResponse.getName());
                        welcomeTextView.setText(saludo + " " + userResponse.getName() + "!");
                    } else {
                        Log.w("HomeActivity", "Respuesta de información de usuario exitosa pero cuerpo nulo o nombre nulo.");
                        welcomeTextView.setText(saludo + " Usuario!");
                    }
                } else {
                    Log.e("HomeActivity", "Error obteniendo información del usuario. Código: " + response.code() + ", Mensaje: " + response.message());
                    if (response.code() == 401) {
                        realizarLogout();
                    } else {
                        welcomeTextView.setText(saludo + " Usuario!");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Log.e("HomeActivity", "Error de conexión al obtener información del usuario: " + t.getMessage());
                welcomeTextView.setText(saludo + " Usuario!");
                Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}