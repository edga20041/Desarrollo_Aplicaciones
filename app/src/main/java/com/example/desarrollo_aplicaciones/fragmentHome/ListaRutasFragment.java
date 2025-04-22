package com.example.desarrollo_aplicaciones.fragmentHome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Ruta;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ListaRutasFragment extends Fragment implements OnMapReadyCallback {

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    private List<Ruta> listaRutas = new ArrayList<>();
    private int indiceRutaActual = 0;
    private Ruta rutaAceptada = null;
    private Polyline rutaPolyline;

    private MapView mapViewEntregaCard;
    private GoogleMap mMap;
    private TextView textViewOrigenCard;
    private TextView textViewDestinoCard;
    private TextView textViewContenidoCard;
    private Button btnRechazarEntrega;
    private Button btnAceptarEntrega;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver rutaFinalizadaReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ListaRutasFragment", "onCreate() llamado.");
        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext());
        registrarBroadcastReceiver();
    }

    private void registrarBroadcastReceiver() {
        rutaFinalizadaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ListaRutasFragment", "onReceive: Broadcast recibido: " + intent.getAction());
                if ("ruta_finalizada".equals(intent.getAction())) {
                    Log.d("ListaRutasFragment", "onReceive: Acción 'ruta_finalizada', recargando historial de rutas.");
                    obtenerRutasDesdeBackend();
                    Log.d("ListaRutasFragment", "onReceive: Se llamó a obtenerRutasDesdeBackend()");
                }
            }
        };
        localBroadcastManager.registerReceiver(rutaFinalizadaReceiver, new IntentFilter("ruta_finalizada"));
        Log.d("ListaRutasFragment", "BroadcastReceiver registrado para 'ruta_finalizada'.");
    }

    private void unregisterBroadcastReceiver() {
        if (rutaFinalizadaReceiver != null && localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(rutaFinalizadaReceiver);
            Log.d("ListaRutasFragment", "BroadcastReceiver desregistrado para 'ruta_finalizada'.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ListaRutasFragment", "onCreateView() llamado.");
        View view = inflater.inflate(R.layout.item_entrega_card, container, false);
        mapViewEntregaCard = view.findViewById(R.id.mapViewEntregaCard);
        textViewOrigenCard = view.findViewById(R.id.textViewOrigenCard);
        textViewDestinoCard = view.findViewById(R.id.textViewDestinoCard);
        textViewContenidoCard = view.findViewById(R.id.textViewContenidoCard);
        btnRechazarEntrega = view.findViewById(R.id.btnRechazarEntrega);
        btnAceptarEntrega = view.findViewById(R.id.btnAceptarEntrega);

        mapViewEntregaCard.onCreate(savedInstanceState);
        mapViewEntregaCard.getMapAsync(this);

        btnAceptarEntrega.setOnClickListener(v -> {
            Log.d("ListaRutasFragment", "Botón Aceptar Entrega presionado.");
            if (listaRutas != null && !listaRutas.isEmpty() && indiceRutaActual < listaRutas.size()) {
                Log.d("ListaRutasFragment", "Intentando aceptar ruta con ID: " + listaRutas.get(indiceRutaActual).getId());
                aceptarRutaEnBackend(listaRutas.get(indiceRutaActual).getId());
            } else {
                Log.w("ListaRutasFragment", "No hay rutas disponibles para aceptar o índice fuera de rango.");
                Toast.makeText(getContext(), "No hay rutas para aceptar", Toast.LENGTH_SHORT).show();
            }
        });

        btnRechazarEntrega.setOnClickListener(v -> {
            Log.d("ListaRutasFragment", "Botón Rechazar Entrega presionado.");
            if (listaRutas != null && !listaRutas.isEmpty() && indiceRutaActual < listaRutas.size()) {
                Log.d("ListaRutasFragment", "Intentando rechazar ruta con ID: " + listaRutas.get(indiceRutaActual).getId());
                rechazarRutaEnBackend(listaRutas.get(indiceRutaActual).getId());
            } else {
                Log.w("ListaRutasFragment", "No hay rutas disponibles para rechazar o índice fuera de rango.");
                Toast.makeText(getContext(), "No hay rutas para rechazar", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ListaRutasFragment", "onViewCreated() llamado.");
    }

    private void obtenerRutasDesdeBackend() {
        String token = tokenRepository.getToken();
        Log.d("ListaRutasFragment", "obtenerRutasDesdeBackend() llamado. Token: " + token);
        if (token != null) {
            Call<List<Ruta>> call = apiService.getRutasPendientes("Bearer " + token);
            Log.d("ListaRutasFragment", "Llamando a getHistorialRutas()...");
            call.enqueue(new Callback<List<Ruta>>() {
                @Override
                public void onResponse(@NonNull Call<List<Ruta>> call, @NonNull Response<List<Ruta>> response) {
                    Log.d("ListaRutasFragment", "Respuesta de getHistorialRutas(): Código " + response.code());
                    Log.d("ListaRutasFragment", "Respuesta Headers: " + response.headers());
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("ListaRutasFragment", "Respuesta exitosa de getHistorialRutas(). Cuerpo: " + response.body());
                        listaRutas.clear();
                        listaRutas.addAll(response.body());
                        Log.d("ListaRutasFragment", "Número de rutas en el historial recibidas: " + listaRutas.size());
                        mostrarRutaActual();
                    } else {
                        Log.e("ListaRutasFragment", "Error en getHistorialRutas(): Código=" + response.code() + ", Mensaje=" + response.message());
                        try {
                            Log.e("ListaRutasFragment", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("ListaRutasFragment", "Error al leer el Error Body", e);
                        }
                        Toast.makeText(getContext(), "Error al cargar el historial de rutas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Ruta>> call, @NonNull Throwable t) {
                    Log.e("ListaRutasFragment", "Error de conexión en getHistorialRutas(): " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("ListaRutasFragment", "Token no encontrado en obtenerRutasDesdeBackend().");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarRutaActual() {
        Log.d("ListaRutasFragment", "mostrarRutaActual() llamado.");
        if (rutaAceptada != null) {
            Log.d("ListaRutasFragment", "Ya se aceptó una ruta, mostrando la aceptada.");
            mostrarRuta(rutaAceptada);
            btnAceptarEntrega.setVisibility(View.GONE);
            btnRechazarEntrega.setVisibility(View.GONE);
            textViewContenidoCard.setText("Ya aceptaste esta ruta. Vuelve a la pantalla principal.");
        } else if (listaRutas != null && !listaRutas.isEmpty() && indiceRutaActual < listaRutas.size()) {
            Log.d("ListaRutasFragment", "Mostrando ruta en índice: " + indiceRutaActual + ", ID: " + listaRutas.get(indiceRutaActual).getId());
            mostrarRuta(listaRutas.get(indiceRutaActual));
            btnAceptarEntrega.setVisibility(View.VISIBLE);
            btnRechazarEntrega.setVisibility(View.VISIBLE);
        } else {
            Log.w("ListaRutasFragment", "No hay más rutas disponibles para mostrar.");
            textViewOrigenCard.setText("No hay más rutas disponibles.");
            textViewDestinoCard.setText("");
            textViewContenidoCard.setText("");
            if (mMap != null) mMap.clear();
            btnAceptarEntrega.setVisibility(View.GONE);
            btnRechazarEntrega.setVisibility(View.GONE);
        }
    }

    private void mostrarRuta(Ruta ruta) {
        Log.d("ListaRutasFragment", "mostrarRuta() llamado para ruta ID: " + ruta.getId() + ", Origen: " + ruta.getOrigen() + ", Destino: " + ruta.getDestino());

        Log.d("ListaRutasFragment", "Mostrando ruta - Origen: " + ruta.getOrigen());
        Log.d("ListaRutasFragment", "Mostrando ruta - Destino: " + ruta.getDestino());
        Log.d("ListaRutasFragment", "Mostrando ruta - Descripcion: " + ruta.getDescripcion());
        Log.d("ListaRutasFragment", "Mostrando ruta - Latitud Origen: " + ruta.getLatitudOrigen());
        Log.d("ListaRutasFragment", "Mostrando ruta - Longitud Origen: " + ruta.getLongitudOrigen());
        Log.d("ListaRutasFragment", "Mostrando ruta - Latitud Destino: " + ruta.getLatitudDestino());
        Log.d("ListaRutasFragment", "Mostrando ruta - Longitud Destino: " + ruta.getLongitudDestino());

        textViewOrigenCard.setText("Origen: " + ruta.getOrigen());
        textViewDestinoCard.setText("Destino: " + ruta.getDestino());
        textViewContenidoCard.setText(ruta.getDescripcion() != null ? "Descripción: " + ruta.getDescripcion() : "");

        Double latOrigen = ruta.getLatitudOrigen();
        Double lonOrigen = ruta.getLongitudOrigen();
        Double latDestino = ruta.getLatitudDestino();
        Double lonDestino = ruta.getLongitudDestino();

        if (mMap != null) {
            mMap.clear();
            if (latOrigen != null && lonOrigen != null) {
                LatLng origen = new LatLng(latOrigen, lonOrigen);
                mMap.addMarker(new MarkerOptions().position(origen).title("Origen: " + ruta.getOrigen()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 14));
                if (latDestino != null && lonDestino != null) {
                    LatLng destino = new LatLng(latDestino, lonDestino);
                    mMap.addMarker(new MarkerOptions().position(destino).title("Destino: " + ruta.getDestino()));
                    mMap.addPolyline(new PolylineOptions().add(origen).add(destino).color(android.graphics.Color.BLUE).width(12f));
                    LatLng centroMapa = new LatLng(
                            (origen.latitude + destino.latitude) / 2,
                            (origen.longitude + destino.longitude) / 2
                    );
                    float zoomLevel = 12;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, zoomLevel));
                }
            } else if (latDestino != null && lonDestino != null) {
                LatLng destino = new LatLng(latDestino, lonDestino);
                mMap.addMarker(new MarkerOptions().position(destino).title("Destino: " + ruta.getDestino()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 14));
            } else {
                Log.w("ListaRutasFragment", "Coordenadas de origen y destino nulas para la ruta: " + ruta.getId());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34.6037, -58.3816), 10));
            }
        }
    }

    private void pasarASiguienteRuta() {
        Log.d("ListaRutasFragment", "pasarASiguienteRuta() llamado. Índice actual antes: " + indiceRutaActual);
        indiceRutaActual++;
        Log.d("ListaRutasFragment", "Índice actual después: " + indiceRutaActual);
        mostrarRutaActual();
    }
    private void aceptarRutaEnBackend(Long rutaId) {
        String token = tokenRepository.getToken();
        Log.d("ListaRutasFragment", "aceptarRutaEnBackend() llamado para ruta ID: " + rutaId + ". Token: " + token);
        if (token != null) {
            Call<ResponseBody> call = apiService.aceptarRuta("Bearer " + token, rutaId);
            Log.d("ListaRutasFragment", "Llamando a aceptarRuta() con ID: " + rutaId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d("ListaRutasFragment", "Respuesta de aceptarRuta(): Código " + response.code());
                    if (response.isSuccessful()) {
                        Log.i("ListaRutasFragment", "Ruta con ID " + rutaId + " aceptada exitosamente en el backend.");
                        // **Descomenta estas líneas**
                        requireActivity().recreate();
                        if (getActivity() != null) {
                            Log.d("ListaRutasFragment", "Finalizando la actividad actual.");
                            getActivity().finish();
                        } else {
                            Log.w("ListaRutasFragment", "getActivity() es nulo, no se puede finalizar la actividad.");
                        }
                        // **Comenta estas líneas**
                        // Intent intent = new Intent(getContext(), HomeActivity.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        // Log.d("ListaRutasFragment", "Iniciando Intent a HomeActivity.");
                        // startActivity(intent);
                        // Log.d("ListaRutasFragment", "Intent a HomeActivity iniciado.");
                    } else {
                        Log.e("ListaRutasFragment", "Error en aceptarRuta(): Código=" + response.code() + ", Mensaje=" + response.message());
                        Toast.makeText(getContext(), "Error al aceptar la ruta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("ListaRutasFragment", "Error de conexión en aceptarRuta(): " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("ListaRutasFragment", "Token no encontrado en aceptarRutaEnBackend().");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }

    private void rechazarRutaEnBackend(Long rutaId) {

        String token = tokenRepository.getToken();

        Log.d("ListaRutasFragment", "rechazarRutaEnBackend() llamado para ruta ID: " + rutaId + ". Token: " + token);

        if (token != null) {

            Call<ResponseBody> call = apiService.rechazarRuta("Bearer " + token, rutaId);

            Log.d("ListaRutasFragment", "Llamando a rechazarRuta() con ID: " + rutaId);

            call.enqueue(new Callback<ResponseBody>() {

                @Override

                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    Log.d("ListaRutasFragment", "Respuesta de rechazarRuta(): Código " + response.code());

                    if (response.isSuccessful()) {

                        Log.i("ListaRutasFragment", "Ruta con ID " + rutaId + " rechazada exitosamente en el backend.");

                        pasarASiguienteRuta();

                    } else {

                        Log.e("ListaRutasFragment", "Error en rechazarRuta(): Código=" + response.code() + ", Mensaje=" + response.message());

                        Toast.makeText(getContext(), "Error al rechazar la ruta", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override

                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                    Log.e("ListaRutasFragment", "Error de conexión en rechazarRuta(): " + t.getMessage());

                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                }

            });

        } else {

            Log.e("ListaRutasFragment", "Token no encontrado en rechazarRutaEnBackend().");

            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Log.d("ListaRutasFragment", "onMapReady() llamado.");
        mostrarRutaActual();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("ListaRutasFragment", "onStart() llamado.");
        mapViewEntregaCard.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ListaRutasFragment", "onResume() llamado.");
        obtenerRutasDesdeBackend();
        mapViewEntregaCard.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ListaRutasFragment", "onPause() llamado.");
        unregisterBroadcastReceiver();
        mapViewEntregaCard.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("ListaRutasFragment", "onStop() llamado.");
        mapViewEntregaCard.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ListaRutasFragment", "onDestroy() llamado.");
        unregisterBroadcastReceiver();
        mapViewEntregaCard.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("ListaRutasFragment", "onLowMemory() llamado.");
        mapViewEntregaCard.onLowMemory();
    }
}