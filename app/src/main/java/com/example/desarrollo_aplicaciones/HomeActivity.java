package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.api.model.UserResponse;
import com.example.desarrollo_aplicaciones.fragmentHome.ListaEntregasFragment;
//import com.example.desarrollo_aplicaciones.repository.auth.HistorialEntregasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.HistorialEntregasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "ANTES de setContentView()");
        try {
            setContentView(R.layout.activity_home);
            Log.d("HomeActivity", "DESPUÉS de setContentView()");

            welcomeTextView = findViewById(R.id.welcomeTextView);
            Button logoutButton = findViewById(R.id.logoutButton);
            ImageView userImageView = findViewById(R.id.userImageView);
            btnVerHistorial = findViewById(R.id.btn_ver_historial);
            btnVerEntregasPendientes = findViewById(R.id.btn_ver_rutas);
            btnVerEntregasPendientes.setText("Ver Entregas");
            btnFinalizarRuta = findViewById(R.id.btnFinalizarRuta);
            contenedorHistorial = findViewById(R.id.contenedorHistorial);
            fragmentContainer = findViewById(R.id.fragment_container);
            layoutEntregaActual = findViewById(R.id.layoutRutaAsignada); // Reutilizamos el layout, cambiar nombres si es necesario
            textViewEntregaActualClienteTexto = findViewById(R.id.textViewRutaAsignadaOrigenTexto); // Reutilizar, ajustar texto
            textViewEntregaActualCliente = findViewById(R.id.textViewRutaAsignadaOrigen); // Reutilizar
            textViewEntregaActualProductoTexto = findViewById(R.id.textViewRutaAsignadaDestinoTexto); // Reutilizar, ajustar texto
            textViewEntregaActualProducto = findViewById(R.id.textViewRutaAsignadaDestino); // Reutilizar

            textViewEntregaActualClienteTexto.setText("Cliente:");
            textViewEntregaActualProductoTexto.setText("Producto:");
            layoutEntregaActual.setVisibility(View.GONE);
            btnFinalizarRuta.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.GONE);

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
                // verificarRutaAsignada(); // Ya no verificamos ruta asignada directamente aquí
            } else {
                Log.w("HomeActivity", "Token no encontrado en onCreate");
                welcomeTextView.setText(saludoDinamico + " Usuario");
            }

            logoutButton.setOnClickListener(v -> {
                Log.d("HomeActivity", "Botón de logout presionado");
                tokenRepository.clearToken();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });

            userImageView.setImageResource(R.drawable.img);

            btnVerHistorial.setOnClickListener(v -> {
                Log.d("HomeActivity", "Botón Ver Historial presionado");
                contenedorHistorial.setVisibility(View.VISIBLE);
                fragmentContainer.setVisibility(View.GONE);
                layoutEntregaActual.setVisibility(View.GONE);
                btnFinalizarRuta.setVisibility(View.GONE);
                cargarHistorialEntregasFragment();
            });

            btnVerEntregasPendientes.setOnClickListener(v -> {
                Log.d("HomeActivity", "Botón Ver Entregas presionado");
                contenedorHistorial.setVisibility(View.GONE);
                layoutEntregaActual.setVisibility(View.GONE);
                btnFinalizarRuta.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                cargarListaEntregasFragment();
            });

            btnFinalizarRuta.setOnClickListener(v -> {
                Log.d("HomeActivity", "Botón Finalizar Entrega presionado");
                // Implementar lógica para finalizar la entrega actual (si es necesario mostrarla aquí)
                Toast.makeText(this, "Funcionalidad para finalizar entrega actual.", Toast.LENGTH_SHORT).show();
                // Si tienes una forma de saber cuál es la entrega actual, llama a una función para finalizarla en el backend
                // finalizarEntregaEnBackend(idEntregaActual);
            });

        } catch (Exception e) {
            Log.e("HomeActivity", "¡ERROR FATAL EN onCreate()!: " + e.getMessage(), e);
            finish();
        }
    }

    private void cargarHistorialEntregasFragment() {
        Log.d("HomeActivity", "cargarHistorialEntregasFragment() llamado");
        HistorialEntregasFragment historialFragment = new HistorialEntregasFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorHistorial, historialFragment);
        fragmentTransaction.commit();
        Log.d("HomeActivity", "Fragment de historial cargado.");
    }

    private void cargarListaEntregasFragment() {
        Log.d("HomeActivity", "cargarListaEntregasFragment() llamado");
        ListaEntregasFragment listaEntregasFragment = new ListaEntregasFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, listaEntregasFragment);
        transaction.commit();
        Log.d("HomeActivity", "Fragment de lista de entregas cargado.");
    }

    private void obtenerInfoUsuario(String token, String saludo) {
        Log.d("HomeActivity", "obtenerInfoUsuario() llamado con token: " + token);
        Call<UserResponse> call = apiService.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.d("HomeActivity", "Respuesta de obtenerInfoUsuario(): Código " + response.code());
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    if (userResponse != null && userResponse.getName() != null) {
                        Log.d("HomeActivity", "Información del usuario recibida: " + userResponse.getName());
                        welcomeTextView.setText(saludo + " " + userResponse.getName() + "!");
                    } else {
                        Log.w("HomeActivity", "Respuesta de información de usuario exitosa pero cuerpo nulo o nombre nulo.");
                        welcomeTextView.setText(saludo + " Usuario");
                    }
                } else {
                    Log.e("HomeActivity", "Error obteniendo información del usuario. Código: " + response.code() + ", Mensaje: " + response.message());
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
}

/*package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.api.model.RutaAsignadaResponse;
import com.example.desarrollo_aplicaciones.api.model.UserResponse;
import com.example.desarrollo_aplicaciones.entity.Ruta;
import com.example.desarrollo_aplicaciones.fragmentHome.ListaRutasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.HistorialEntregasFragment;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;

import java.time.LocalTime;
import java.time.ZoneId;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
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
    private TextView textViewRutaAsignadaOrigenTexto;
    private TextView textViewRutaAsignadaOrigen;
    private TextView textViewRutaAsignadaDestinoTexto;
    private TextView textViewRutaAsignadaDestino;
    private FrameLayout contenedorHistorial;
    private FrameLayout fragmentContainer;
    private View layoutRutaAsignada;

    private RutaAsignadaResponse rutaAsignadaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "ANTES de setContentView()");
        try {
            setContentView(R.layout.activity_home);
            Log.d("HomeActivity", "DESPUÉS de setContentView()");

            welcomeTextView = findViewById(R.id.welcomeTextView);
            Button logoutButton = findViewById(R.id.logoutButton);
            ImageView userImageView = findViewById(R.id.userImageView);
            btnVerHistorial = findViewById(R.id.btn_ver_historial);
            btnVerEntregasPendientes = findViewById(R.id.btn_ver_rutas);
            btnVerEntregasPendientes.setText("Ver Entregas");
            btnFinalizarRuta = findViewById(R.id.btnFinalizarRuta);
            textViewRutaAsignadaOrigenTexto = findViewById(R.id.textViewRutaAsignadaOrigenTexto);
            textViewRutaAsignadaOrigen = findViewById(R.id.textViewRutaAsignadaOrigen);
            textViewRutaAsignadaDestinoTexto = findViewById(R.id.textViewRutaAsignadaDestinoTexto);
            textViewRutaAsignadaDestino = findViewById(R.id.textViewRutaAsignadaDestino);
            contenedorHistorial = findViewById(R.id.contenedorHistorial);
            fragmentContainer = findViewById(R.id.fragment_container);
            layoutRutaAsignada = findViewById(R.id.layoutRutaAsignada);

            layoutRutaAsignada.setVisibility(View.GONE);
            btnFinalizarRuta.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.GONE);

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
            verificarRutaAsignada(); // Verificar si hay una ruta asignada al iniciar
        } else {
            Log.w("HomeActivity", "Token no encontrado en onCreate");
            welcomeTextView.setText(saludoDinamico + " Usuario");
        }

        logoutButton.setOnClickListener(v -> {
            Log.d("HomeActivity", "Botón de logout presionado");
            tokenRepository.clearToken();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        userImageView.setImageResource(R.drawable.img);

        btnVerHistorial.setOnClickListener(v -> {
            Log.d("HomeActivity", "Botón Ver Historial presionado");
            contenedorHistorial.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            layoutRutaAsignada.setVisibility(View.GONE);
            btnFinalizarRuta.setVisibility(View.GONE);
            cargarHistorialEntregasFragment();
        });

        btnVerEntregasPendientes.setOnClickListener(v -> {
            Log.d("HomeActivity", "Botón Ver Entregas presionado");
            contenedorHistorial.setVisibility(View.GONE);
            layoutRutaAsignada.setVisibility(View.GONE);
            btnFinalizarRuta.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
            cargarListaEntregasFragment();
        });

        btnFinalizarRuta.setOnClickListener(v -> {
            Log.d("HomeActivity", "Botón Finalizar Ruta presionado");
            if (rutaAsignadaActual != null) {
                Log.d("HomeActivity", "Intentando finalizar ruta con ID: " + rutaAsignadaActual.getId());
                finalizarRutaEnBackend(rutaAsignadaActual.getId());
            } else {
                Log.w("HomeActivity", "No hay ruta asignada para finalizar.");
                Toast.makeText(this, "No hay ruta asignada para finalizar", Toast.LENGTH_SHORT).show();
            }

        });
        } catch (Exception e) {
            Log.e("HomeActivity", "¡ERROR FATAL EN onCreate()!: " + e.getMessage(), e);
            // Aquí podrías mostrar un mensaje de error al usuario si es posible
            // O realizar alguna acción de limpieza antes de finalizar
            finish(); // Asegúrate de que la actividad se cierre después de loguear el error
        }
    }


    private void verificarRutaAsignada() {
        Log.d("HomeActivity", "verificarRutaAsignada() llamado");
        String token = tokenRepository.getToken();
        if (token != null) {
            Log.d("HomeActivity", "Token para verificar ruta: " + token);
            Call<RutaAsignadaResponse> call = apiService.getRutaAsignada("Bearer " + token);
            call.enqueue(new Callback<RutaAsignadaResponse>() {
                @Override
                public void onResponse(Call<RutaAsignadaResponse> call, Response<RutaAsignadaResponse> response) {
                    Log.d("HomeActivity", "Respuesta de verificarRutaAsignada(): Código " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        rutaAsignadaActual = response.body();
                        Ruta ruta = rutaAsignadaActual.getRuta();
                        if (ruta != null) {
                            Log.d("HomeActivity", "Ruta asignada encontrada: Origen=" + ruta.getOrigen() + ", Destino=" + ruta.getDestino());
                            mostrarRutaAsignada(ruta);
                        } else {
                            Log.w("HomeActivity", "La ruta asignada no contiene información de la ruta.");
                            btnVerEntregasPendientes.setVisibility(View.VISIBLE);
                            layoutRutaAsignada.setVisibility(View.GONE);
                            btnFinalizarRuta.setVisibility(View.GONE);
                            fragmentContainer.setVisibility(View.GONE);
                        }
                    } else if (response.code() == 404) {
                        Log.i("HomeActivity", "No hay ruta asignada actualmente (código 404).");
                        btnVerEntregasPendientes.setVisibility(View.VISIBLE);
                        fragmentContainer.setVisibility(View.GONE);
                        layoutRutaAsignada.setVisibility(View.GONE);
                        btnFinalizarRuta.setVisibility(View.GONE);
                    } else {
                        Log.e("HomeActivity", "Error al verificar ruta asignada: Código=" + response.code() + ", Mensaje=" + response.message());
                        Toast.makeText(HomeActivity.this, "Error al verificar la ruta asignada", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RutaAsignadaResponse> call, Throwable t) {
                    Log.e("HomeActivity", "Error de conexión al verificar ruta asignada: " + t.getMessage());
                    Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w("HomeActivity", "Token es nulo en verificarRutaAsignada()");
        }
    }
    private void finalizarRutaEnBackend(Long rutaAsignadaId) {
        String token = tokenRepository.getToken();
        if (token != null) {
            Log.d("HomeActivity", "Token para finalizar ruta: " + token);
            Call<ResponseBody> call = apiService.finalizarRuta("Bearer " + token, rutaAsignadaId);
            Log.d("HomeActivity", "Llamando a finalizarRuta con ID: " + rutaAsignadaId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("HomeActivity", "Respuesta de finalizarRuta(): Código " + response.code());
                    if (response.isSuccessful()) {
                        Log.i("HomeActivity", "Ruta finalizada exitosamente.");
                        Toast.makeText(HomeActivity.this, "Ruta finalizada", Toast.LENGTH_SHORT).show();
                        rutaAsignadaActual = null;
                        verificarRutaAsignada();
                    } else {
                        Log.e("HomeActivity", "Error al finalizar la ruta: Código=" + response.code() + ", Mensaje=" + response.message());
                        Toast.makeText(HomeActivity.this, "Error al finalizar la ruta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("HomeActivity", "Error de conexión al finalizar la ruta: " + t.getMessage());
                    Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w("HomeActivity", "Token es nulo al finalizar la ruta.");
        }
    }

    private void mostrarRutaAsignada(Ruta ruta) {
        Log.d("HomeActivity", "mostrarRutaAsignada() llamado con ruta: Origen=" + ruta.getOrigen() + ", Destino=" + ruta.getDestino());
        if (ruta != null) {
            textViewRutaAsignadaOrigen.setText(ruta.getOrigen());
            textViewRutaAsignadaDestino.setText(ruta.getDestino());
            layoutRutaAsignada.setVisibility(View.VISIBLE);
            btnFinalizarRuta.setVisibility(View.VISIBLE);
            btnVerEntregasPendientes.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.GONE);
            contenedorHistorial.setVisibility(View.GONE);
        } else {
            Log.w("HomeActivity", "mostrarRutaAsignada() recibió una ruta nula.");
            btnVerEntregasPendientes.setVisibility(View.VISIBLE);
            layoutRutaAsignada.setVisibility(View.GONE);
            btnFinalizarRuta.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.GONE);
        }
    }

    private void cargarHistorialEntregasFragment() {
        Log.d("HomeActivity", "cargarHistorialEntregasFragment() llamado");
        HistorialEntregasFragment historialFragment = new HistorialEntregasFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorHistorial, historialFragment);
        fragmentTransaction.commit();
        Log.d("HomeActivity", "Fragment de historial cargado.");
    }

    private void cargarListaEntregasFragment() {
        Log.d("HomeActivity", "cargarListaEntregasFragment() llamado");
        ListaRutasFragment listaRutasFragment = new ListaRutasFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, listaRutasFragment);
        transaction.commit();
        Log.d("HomeActivity", "Fragment de lista de entregas cargado.");
    }

    private void obtenerInfoUsuario(String token, String saludo) {
        Log.d("HomeActivity", "obtenerInfoUsuario() llamado con token: " + token);
        Call<UserResponse> call = apiService.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.d("HomeActivity", "Respuesta de obtenerInfoUsuario(): Código " + response.code());
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    if (userResponse != null && userResponse.getName() != null) {
                        Log.d("HomeActivity", "Información del usuario recibida: " + userResponse.getName());
                        welcomeTextView.setText(saludo + " " + userResponse.getName() + "!");
                    } else {
                        Log.w("HomeActivity", "Respuesta de información de usuario exitosa pero cuerpo nulo o nombre nulo.");
                        welcomeTextView.setText(saludo + " Usuario");
                    }
                } else {
                    Log.e("HomeActivity", "Error obteniendo información del usuario. Código: " + response.code() + ", Mensaje: " + response.message());
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
} */