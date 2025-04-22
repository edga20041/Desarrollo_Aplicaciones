package com.example.desarrollo_aplicaciones.fragmentHome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Ruta;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@AndroidEntryPoint
public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    @Inject
    ApiService apiService;
    @Inject
    SharedPreferences sharedPreferences; // Inyecta SharedPreferences

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        cargarRutasPendientes(); // Llama al método correcto
    }

    private void cargarRutasPendientes() {
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            Call<List<Ruta>> call = apiService.getRutasPendientes("Bearer " + token); // Usa getRutasPendientes con el token

            call.enqueue(new Callback<List<Ruta>>() {
                @Override
                public void onResponse(Call<List<Ruta>> call, Response<List<Ruta>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Ruta> rutas = response.body();
                        for (Ruta ruta : rutas) {
                            LatLng coordenadas = new LatLng(ruta.getLatitudDestino(), ruta.getLongitudDestino()); // Usa las coordenadas de destino para el marcador
                            googleMap.addMarker(new MarkerOptions()
                                    .position(coordenadas)
                                    .title(ruta.getNombre())
                                    .snippet("Destino: " + ruta.getDestino()));
                        }
                        if (!rutas.isEmpty()) {
                            LatLng primeraRuta = new LatLng(rutas.get(0).getLatitudDestino(), rutas.get(0).getLongitudDestino());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeraRuta, 12));
                        }
                    } else {
                        Log.e("MapaFragment", "Error al cargar rutas pendientes: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Ruta>> call, Throwable t) {
                    Log.e("MapaFragment", "Error de conexión al cargar rutas pendientes", t);
                }
            });
        } else {
            Log.e("MapaFragment", "Token no encontrado.");
            // Manejar el caso en que no hay token (por ejemplo, redirigir al login)
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}