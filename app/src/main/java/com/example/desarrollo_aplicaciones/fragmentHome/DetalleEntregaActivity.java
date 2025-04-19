package com.example.desarrollo_aplicaciones.fragmentHome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.api.model.CambiarEstadoEntregaRequest;
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.example.desarrollo_aplicaciones.entity.Estado; // Importa la clase Estado
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class DetalleEntregaActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    private TextView textCliente;
    private TextView textClienteDni;
    private TextView textProducto;
    private TextView textRutaId;
    private TextView textEstadoId;
    private TextView textFechaCreacion;
    private TextView textFechaAsignacion;
    private Button btnVolver;
    private Button btnQr;

    private Long entregaId;
    private Long repartidorIdEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_entrega);

        textCliente = findViewById(R.id.textCliente);
        textClienteDni = findViewById(R.id.textClienteDni);
        textProducto = findViewById(R.id.textProducto);
        textRutaId = findViewById(R.id.textRutaId);
        textEstadoId = findViewById(R.id.textEstadoId);
        textFechaCreacion = findViewById(R.id.textFechaCreacion);
        textFechaAsignacion = findViewById(R.id.textFechaAsignacion);
        btnVolver = findViewById(R.id.btnVolver);
        btnQr = findViewById(R.id.btnQr);

        entregaId = getIntent().getLongExtra("entrega_id", -1);

        if (entregaId != -1) {
            cargarDetalleEntrega(entregaId);
        } else {
            Toast.makeText(this, "Error: ID de entrega no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnVolver.setOnClickListener(v -> finish());

        btnQr.setOnClickListener(v -> {
            Log.d("DetalleEntregaActivity", "Botón QR presionado.");
            finalizarEntrega();
        });
    }

    private void finalizarEntrega() {
        String token = tokenRepository.getToken();
        Long estadoFinalizadoId = 3L;

        CambiarEstadoEntregaRequest request = new CambiarEstadoEntregaRequest();
        request.setEntregaId(entregaId);
        request.setEstadoId(estadoFinalizadoId);
        request.setRepartidorId(repartidorIdEntrega);

        Log.d("FinalizarEntrega", "Valor de repartidorIdEntrega antes de la request: " + repartidorIdEntrega);
        Call<com.example.desarrollo_aplicaciones.api.model.CambiarEstadoEntregaResponse> call =
                apiService.cambiarEstadoEntrega(request, "Bearer " + token);

        call.enqueue(new Callback<com.example.desarrollo_aplicaciones.api.model.CambiarEstadoEntregaResponse>() {
            @Override
            public void onResponse(Call<com.example.desarrollo_aplicaciones.api.model.CambiarEstadoEntregaResponse> call, Response<com.example.desarrollo_aplicaciones.api.model.CambiarEstadoEntregaResponse> response) {
                Log.d("FinalizarEntrega", "Respuesta de cambiarEstadoEntrega: Código " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FinalizarEntrega", "Entrega finalizada exitosamente: " + response.body().getMessage());
                    Toast.makeText(DetalleEntregaActivity.this, "Entrega finalizada", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("entrega_finalizada");
                    LocalBroadcastManager.getInstance(DetalleEntregaActivity.this).sendBroadcast(intent);

                    finish();
                } else {
                    Log.e("FinalizarEntrega", "Error al finalizar la entrega. Código: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("FinalizarEntrega", "Error del backend: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("FinalizarEntrega", "Error al leer el error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.desarrollo_aplicaciones.api.model.CambiarEstadoEntregaResponse> call, Throwable t) {
                Log.e("FinalizarEntrega", "Error de conexión al finalizar: " + t.getMessage()); // Log de error de conexión
                Toast.makeText(DetalleEntregaActivity.this, "Error de conexión al finalizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDetalleEntrega(Long id) {
        String token = tokenRepository.getToken();
        Call<Entrega> call = apiService.getEntregaById(id, "Bearer " + token);
        call.enqueue(new Callback<Entrega>() {
            @Override
            public void onResponse(Call<Entrega> call, Response<Entrega> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Entrega entrega = response.body();
                    Log.d("DetalleEntregaActivity", "Repartidor ID recibido del backend: " + entrega.getRepartidorId());
                    repartidorIdEntrega = entrega.getRepartidorId();
                    cargarNombreEstado(entrega.getEstadoId());
                    mostrarDetalleEntregaBasico(entrega);
                } else {
                    Toast.makeText(DetalleEntregaActivity.this, "Error al cargar detalle de entrega", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Entrega> call, Throwable t) {
                Toast.makeText(DetalleEntregaActivity.this, "Error de conexión al cargar detalle", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarNombreEstado(Long estadoId) {
        String token = tokenRepository.getToken();
        Call<Estado> call = apiService.getEstado(estadoId, "Bearer " + token);
        call.enqueue(new Callback<Estado>() {
            @Override
            public void onResponse(Call<Estado> call, Response<Estado> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Estado estado = response.body();
                    textEstadoId.setText(estado.getNombre());
                } else {
                    textEstadoId.setText("Estado ID: " + estadoId + " (Error al cargar nombre)");
                }
            }

            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                textEstadoId.setText("Estado ID: " + estadoId + " (Error de conexión)");
            }
        });
    }

    private void mostrarDetalleEntregaBasico(Entrega entrega) {
        textCliente.setText(entrega.getCliente());
        textClienteDni.setText(String.valueOf(entrega.getClienteDni()));
        textProducto.setText(entrega.getProducto());
        textRutaId.setText(String.valueOf(entrega.getRutaId()));
        textFechaCreacion.setText(entrega.getFechaCreacion());
        textFechaAsignacion.setText(entrega.getFechaAsignacion());
    }
}