package com.example.desarrollo_aplicaciones.fragmentHome;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
            if (listaRutas != null && !listaRutas.isEmpty() && indiceRutaActual < listaRutas.size()) {
                aceptarRutaEnBackend(listaRutas.get(indiceRutaActual).getId());
            }
        });

        btnRechazarEntrega.setOnClickListener(v -> {
            if (listaRutas != null && !listaRutas.isEmpty() && indiceRutaActual < listaRutas.size()) {
                rechazarRutaEnBackend(listaRutas.get(indiceRutaActual).getId());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtenerRutasDesdeBackend(); // Obtener las rutas pendientes del backend
    }

    private void obtenerRutasDesdeBackend() {
        String token = tokenRepository.getToken();
        Log.d("ListaRutasFragment", "Token para obtener rutas: " + token);
        if (token != null) {
            Call<List<Ruta>> call = apiService.getRutasPendientes("Bearer " + token);
            Log.d("ListaRutasFragment", "Llamando a la API para obtener rutas...");
            call.enqueue(new Callback<List<Ruta>>() {
                @Override
                public void onResponse(@NonNull Call<List<Ruta>> call, @NonNull Response<List<Ruta>> response) {
                    Log.d("ListaRutasFragment", "Respuesta de la API recibida. Código: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("ListaRutasFragment", "Respuesta exitosa. Cuerpo: " + response.body().toString()); // Imprime el cuerpo de la respuesta
                        listaRutas.clear();
                        listaRutas.addAll(response.body());
                        Log.d("ListaRutasFragment", "Número de rutas recibidas: " + listaRutas.size());
                        mostrarRutaActual();
                    } else {
                        Log.e("ListaRutasFragment", "Error al obtener rutas pendientes: Código: " + response.code() + ", Mensaje: " + response.message());
                        Toast.makeText(getContext(), "Error al cargar las rutas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Ruta>> call, @NonNull Throwable t) {
                    Log.e("ListaRutasFragment", "Error de conexión al obtener rutas pendientes: " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("ListaRutasFragment", "Token no encontrado.");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }

    private void aceptarRutaEnBackend(Long rutaId) {
        String token = tokenRepository.getToken();
        if (token != null) {
            Call<ResponseBody> call = apiService.aceptarRuta("Bearer " + token, rutaId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ListaRutasFragment", "Ruta con ID " + rutaId + " aceptada en el backend.");
                        rutaAceptada = listaRutas.get(indiceRutaActual); // Marcar como aceptada localmente
                        mostrarRutaActual();
                    } else {
                        Log.e("ListaRutasFragment", "Error al aceptar ruta en backend: " + response.code());
                        Toast.makeText(getContext(), "Error al aceptar la ruta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("ListaRutasFragment", "Error de conexión al aceptar ruta: " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("ListaRutasFragment", "Token no encontrado.");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }

    private void rechazarRutaEnBackend(Long rutaId) {
        String token = tokenRepository.getToken();
        if (token != null) {
            Call<ResponseBody> call = apiService.rechazarRuta("Bearer " + token, rutaId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ListaRutasFragment", "Ruta con ID " + rutaId + " rechazada en el backend.");
                        pasarASiguienteRuta();
                    } else {
                        Log.e("ListaRutasFragment", "Error al rechazar ruta en backend: " + response.code());
                        Toast.makeText(getContext(), "Error al rechazar la ruta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("ListaRutasFragment", "Error de conexión al rechazar ruta: " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("ListaRutasFragment", "Token no encontrado.");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostrarRutaActual() {
        if (rutaAceptada != null) {
            mostrarRuta(rutaAceptada);
            btnAceptarEntrega.setVisibility(View.GONE);
            btnRechazarEntrega.setVisibility(View.GONE);
            textViewContenidoCard.setText("Ruta Aceptada: " + rutaAceptada.getDestino());
        } else if (listaRutas != null && !listaRutas.isEmpty() && indiceRutaActual < listaRutas.size()) {
            mostrarRuta(listaRutas.get(indiceRutaActual));
            btnAceptarEntrega.setVisibility(View.VISIBLE);
            btnRechazarEntrega.setVisibility(View.VISIBLE);
        } else {
            textViewOrigenCard.setText("No hay más rutas disponibles.");
            textViewDestinoCard.setText("");
            textViewContenidoCard.setText("");
            if (mMap != null) mMap.clear();
            btnAceptarEntrega.setVisibility(View.GONE);
            btnRechazarEntrega.setVisibility(View.GONE);
        }
    }
    private void mostrarRuta(Ruta ruta) {
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 14)); // Mover la cámara al origen inicialmente
                if (latDestino != null && lonDestino != null) {
                    LatLng destino = new LatLng(latDestino, lonDestino);
                    mMap.addMarker(new MarkerOptions().position(destino).title("Destino: " + ruta.getDestino()));
                    mMap.addPolyline(new PolylineOptions().add(origen).add(destino).color(android.graphics.Color.BLUE).width(12f)); // Dibujar línea recta
                    // Ajustar la cámara para mostrar ambos puntos
                    LatLng centroMapa = new LatLng(
                            (origen.latitude + destino.latitude) / 2,
                            (origen.longitude + destino.longitude) / 2
                    );
                    float zoomLevel = 12; // Puedes ajustar este valor según tus necesidades
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, zoomLevel));
                }
            } else if (latDestino != null && lonDestino != null) {
                LatLng destino = new LatLng(latDestino, lonDestino);
                mMap.addMarker(new MarkerOptions().position(destino).title("Destino: " + ruta.getDestino()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 14)); // Mover la cámara al destino si el origen es nulo
            } else {
                // Si ambas coordenadas son nulas, podrías mostrar un mensaje o no mover la cámara
                Log.w("ListaRutasFragment", "Coordenadas de origen y destino nulas para la ruta: " + ruta.getId());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34.6037, -58.3816), 10)); // Mostrar Buenos Aires por defecto
            }
        }
    }

    private void pasarASiguienteRuta() {
        indiceRutaActual++;
        mostrarRutaActual();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mostrarRutaActual();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapViewEntregaCard.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewEntregaCard.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewEntregaCard.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapViewEntregaCard.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewEntregaCard.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewEntregaCard.onLowMemory();
    }
}