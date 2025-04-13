package com.example.desarrollo_aplicaciones.fragmentHome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.AuthApi;
import com.example.desarrollo_aplicaciones.di.RetrofitClient;
import com.example.desarrollo_aplicaciones.entity.Ruta;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.Nullable;

import javax.inject.Inject;


@AndroidEntryPoint
    public class MapaFragment extends Fragment implements OnMapReadyCallback {

        private MapView mapView;
        private GoogleMap googleMap;
        @Inject
        AuthApi authApi;

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
            cargarRutas();
        }

        private void cargarRutas() {

            Call<List<Ruta>> call = authApi.getRutas();

            call.enqueue(new Callback<List<Ruta>>() {
                @Override
                public void onResponse(Call<List<Ruta>> call, Response<List<Ruta>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Ruta> rutas = response.body();
                        for (Ruta ruta : rutas) {
                            LatLng coordenadas = new LatLng(ruta.getLatitud(), ruta.getLongitud());
                            googleMap.addMarker(new MarkerOptions()
                                    .position(coordenadas)
                                    .title(ruta.getNombre())
                                    .snippet("Destino: " + ruta.getDestino()));
                        }
                        if (!rutas.isEmpty()) {
                            LatLng primeraRuta = new LatLng(rutas.get(0).getLatitud(), rutas.get(0).getLongitud());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeraRuta, 12));
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Ruta>> call, Throwable t) {
                    Log.e("MapaFragment", "Error al cargar rutas", t);
                }
            });
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
