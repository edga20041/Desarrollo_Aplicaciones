package com.example.desarrollo_aplicaciones.repository.auth;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.example.desarrollo_aplicaciones.entity.Estado;
import com.example.desarrollo_aplicaciones.entity.Ruta;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class DetalleEntregaHistorialActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    private TextView textClienteHistorial;
    private TextView textClienteDniHistorial;
    private TextView textProductoHistorial;
    private TextView textRutaIdHistorial;
    private TextView textEstadoIdHistorial;
    private TextView textFechaCreacionHistorial;
    private TextView textFechaAsignacionHistorial;
    private Button btnVolverHistorial;
    private MapView mapViewDetalleHistorial;
    private View mapPlaceholder; // Referencia al placeholder
    private GoogleMap mMap;
    private TextView textViewOrigenHistorial;
    private TextView textViewDestinoHistorial;

    private Long entregaId;
    private Entrega entregaDetalle;
    private Ruta rutaDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_entrega_historial);

        textClienteHistorial = findViewById(R.id.textClienteHistorial);
        textClienteDniHistorial = findViewById(R.id.textClienteDniHistorial);
        textProductoHistorial = findViewById(R.id.textProductoHistorial);
        textRutaIdHistorial = findViewById(R.id.textRutaIdHistorial);
        textEstadoIdHistorial = findViewById(R.id.textEstadoIdHistorial);
        textFechaCreacionHistorial = findViewById(R.id.textFechaCreacionHistorial);
        textFechaAsignacionHistorial = findViewById(R.id.textFechaAsignacionHistorial);
        btnVolverHistorial = findViewById(R.id.btnVolverHistorial);
        mapViewDetalleHistorial = findViewById(R.id.mapViewDetalleHistorial);
        mapPlaceholder = findViewById(R.id.mapPlaceholder); // Obtén la referencia
        textViewOrigenHistorial = findViewById(R.id.textViewOrigenHistorial);
        textViewDestinoHistorial = findViewById(R.id.textViewDestinoHistorial);

        mapViewDetalleHistorial.onCreate(savedInstanceState);
        mapViewDetalleHistorial.getMapAsync(this);

        entregaId = getIntent().getLongExtra("entrega_id", -1);

        if (entregaId != -1) {
            cargarDetalleEntrega(entregaId);
        } else {
            Toast.makeText(this, "Error: ID de entrega no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnVolverHistorial.setOnClickListener(v -> finish());
    }

    private void cargarDetalleEntrega(Long id) {
        String token = tokenRepository.getToken();
        Call<Entrega> call = apiService.getEntregaById(id, "Bearer " + token);
        call.enqueue(new Callback<Entrega>() {
            @Override
            public void onResponse(Call<Entrega> call, Response<Entrega> response) {
                if (response.isSuccessful() && response.body() != null) {
                    entregaDetalle = response.body();
                    cargarNombreEstado(entregaDetalle.getEstadoId());
                    mostrarDetalleEntregaBasico(entregaDetalle);
                    cargarDetalleRuta(entregaDetalle.getRutaId());
                } else {
                    Toast.makeText(DetalleEntregaHistorialActivity.this, "Error al cargar detalle de entrega", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Entrega> call, Throwable t) {
                Toast.makeText(DetalleEntregaHistorialActivity.this, "Error de conexión al cargar detalle", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDetalleRuta(Long rutaId) {
        String token = tokenRepository.getToken();
        Call<Ruta> callRuta = apiService.getRutaById(rutaId, "Bearer " + token);
        callRuta.enqueue(new Callback<Ruta>() {
            @Override
            public void onResponse(Call<Ruta> call, Response<Ruta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rutaDetalle = response.body();
                    if (mMap != null) {
                        configurarMapa();
                        mapViewDetalleHistorial.setVisibility(View.VISIBLE);
                        mapPlaceholder.setVisibility(View.GONE);

                        // Fuerza el redibujo
                        ScrollView scrollView = findViewById(R.id.scrollViewDetalleEntregaHistorial); // Reemplaza con el ID correcto
                        LinearLayout linearLayout = findViewById(R.id.linearLayoutContenedor); // Reemplaza con el ID correcto
                        if (scrollView != null) {
                            scrollView.invalidate();
                            scrollView.requestLayout();
                        }
                        if (linearLayout != null) {
                            linearLayout.invalidate();
                            linearLayout.requestLayout();
                        }

                    }
                } else {
                    Toast.makeText(DetalleEntregaHistorialActivity.this, "Error al cargar detalle de la ruta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ruta> call, Throwable t) {
                Toast.makeText(DetalleEntregaHistorialActivity.this, "Error de conexión al cargar detalle de la ruta", Toast.LENGTH_SHORT).show();
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
                    textEstadoIdHistorial.setText(estado.getNombre());
                } else {
                    textEstadoIdHistorial.setText("Estado ID: " + estadoId + " (Error al cargar nombre)");
                }
            }

            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                textEstadoIdHistorial.setText("Estado ID: " + estadoId + " (Error de conexión)");
            }
        });
    }

    private void mostrarDetalleEntregaBasico(Entrega entrega) {
        textClienteHistorial.setText("Cliente: " + entrega.getCliente());
        textClienteDniHistorial.setText("DNI: " + String.valueOf(entrega.getClienteDni()));
        textProductoHistorial.setText("Producto: " + entrega.getProducto());
        textRutaIdHistorial.setText("Ruta ID: " + String.valueOf(entrega.getRutaId()));
        textFechaCreacionHistorial.setText("Fecha Creación: " + entrega.getFechaCreacion());
        textFechaAsignacionHistorial.setText("Fecha Asignación: " + entrega.getFechaAsignacion());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (rutaDetalle != null) {
            configurarMapa();
            mapViewDetalleHistorial.setVisibility(View.VISIBLE);
            mapPlaceholder.setVisibility(View.GONE);
        }
    }

    private void configurarMapa() {
        if (mMap != null && rutaDetalle != null) {
            LatLng origen = new LatLng(rutaDetalle.getLatitudOrigen(), rutaDetalle.getLongitudOrigen());
            LatLng destino = new LatLng(rutaDetalle.getLatitudDestino(), rutaDetalle.getLongitudDestino());

            mMap.addMarker(new MarkerOptions().position(origen).title("Origen"));
            mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));

            mMap.addPolyline(new PolylineOptions()
                    .add(origen)
                    .add(destino)
                    .color(0xFF0000FF)
                    .width(10));

            LatLng centroMapa = new LatLng((origen.latitude + destino.latitude) / 2, (origen.longitude + destino.longitude) / 2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, 15));

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressesOrigen = geocoder.getFromLocation(origen.latitude, origen.longitude, 1);
                if (addressesOrigen != null && !addressesOrigen.isEmpty()) {
                    textViewOrigenHistorial.setText("Origen: " + addressesOrigen.get(0).getAddressLine(0));
                }
                List<Address> addressesDestino = geocoder.getFromLocation(destino.latitude, destino.longitude, 1);
                if (addressesDestino != null && !addressesDestino.isEmpty()) {
                    textViewDestinoHistorial.setText("Destino: " + addressesDestino.get(0).getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapViewDetalleHistorial.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewDetalleHistorial.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewDetalleHistorial.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewDetalleHistorial.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapViewDetalleHistorial.onSaveInstanceState(outState);
    }
}