package com.example.desarrollo_aplicaciones.fragmentHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.desarrollo_aplicaciones.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class VerRutasFragment extends Fragment implements OnMapReadyCallback {

    private Button btnVerEntrega;
    private MapView mapViewEntrega;
    private GoogleMap mMap;
    private TextView textViewOrigen;
    private TextView textViewDestino;
    private TextView textViewContenido;

    // Datos de la entrega (simulados por ahora)
    private LatLng origen = new LatLng(-34.6037, -58.3816); // Ejemplo: Obelisco, Buenos Aires
    private LatLng destino = new LatLng(-34.6179, -58.3715); // Ejemplo: Plaza de Mayo, Buenos Aires
    private String direccionOrigen = "Obelisco, Buenos Aires";
    private String direccionDestino = "Plaza de Mayo, Buenos Aires";
    private String contenidoEntrega = "Documentos importantes";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_entrega, container, false);
        btnVerEntrega = view.findViewById(R.id.btn_ver_entrega);
        mapViewEntrega = view.findViewById(R.id.mapViewEntrega);
        textViewOrigen = view.findViewById(R.id.textViewOrigen);
        textViewDestino = view.findViewById(R.id.textViewDestino);
        textViewContenido = view.findViewById(R.id.textViewContenido);

        mapViewEntrega.onCreate(savedInstanceState);
        mapViewEntrega.getMapAsync(this);

        btnVerEntrega.setOnClickListener(v -> mostrarDetallesEntrega());

        mostrarDetallesEntrega();

        return view;
    }

    private void mostrarDetallesEntrega() {
        if (mMap != null) {
            mMap.clear();

            mMap.addMarker(new MarkerOptions().position(origen).title("Origen: " + direccionOrigen));
            mMap.addMarker(new MarkerOptions().position(destino).title("Destino: " + direccionDestino));

            // Por ahora, una simple línea recta
            mMap.addPolyline(new PolylineOptions()
                    .add(origen)
                    .add(destino)
                    .color(0xFF0000FF)
                    .width(10));

            LatLng centroMapa = new LatLng((origen.latitude + destino.latitude) / 2, (origen.longitude + destino.longitude) / 2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, 15)); // Ajusta el zoom según necesites
        }

        textViewOrigen.setText("Origen: " + direccionOrigen);
        textViewDestino.setText("Destino: " + direccionDestino);
        textViewContenido.setText("Contenido: " + contenidoEntrega);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewEntrega.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewEntrega.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewEntrega.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewEntrega.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapViewEntrega.onSaveInstanceState(outState);
    }
}