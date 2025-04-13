package com.example.desarrollo_aplicaciones.fragmentHome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.AuthApi;
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ListaEntregasFragment extends Fragment implements OnMapReadyCallback {

    @Inject
    AuthApi authApi;

    private List<Entrega> listaEntregas;
    private int indiceEntregaActual = 0;

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

        listaEntregas = obtenerEntregasSimuladas();
        mostrarEntregaActual();

        btnAceptarEntrega.setOnClickListener(v -> {
            Log.d("ListaEntregasFragment", "Entrega aceptada: " + listaEntregas.get(indiceEntregaActual).getContenido());
            pasarASiguienteEntrega();
        });

        btnRechazarEntrega.setOnClickListener(v -> {
            Log.d("ListaEntregasFragment", "Entrega rechazada: " + listaEntregas.get(indiceEntregaActual).getContenido());
            pasarASiguienteEntrega();
        });

        return view;
    }

    private List<Entrega> obtenerEntregasSimuladas() {
        List<Entrega> entregas = new ArrayList<>();
        entregas.add(new Entrega("09:00", "Juan Pérez", "Pendiente", "Calle Falsa 123, Buenos Aires", "Avenida Siempreviva 742, Springfield", "Documentos"));
        entregas.add(new Entrega("10:30", "María Gómez", "En camino", "Diagonal Sur 500, Buenos Aires", "Ruta 66 Km 10, EEUU", "Paquete pequeño"));
        entregas.add(new Entrega("11:15", "Carlos López", "Entregado", "Corrientes 348, Buenos Aires", "Lavalle 678, Buenos Aires", "Flores"));
        Log.d("ListaEntregasFragment", "Tamaño de la lista de entregas simuladas: " + entregas.size());
        return entregas;
    }

    private void mostrarEntregaActual() {
        if (listaEntregas != null && !listaEntregas.isEmpty() && indiceEntregaActual < listaEntregas.size()) {
            Entrega entrega = listaEntregas.get(indiceEntregaActual);
            Log.d("ListaEntregasFragment", "Mostrando entrega: " + entrega.getContenido());
            textViewOrigenCard.setText("Origen: " + entrega.getDireccionOrigen());
            textViewDestinoCard.setText("Destino: " + entrega.getDireccionDestino());
            textViewContenidoCard.setText("Contenido: " + entrega.getContenido());

            LatLng origen = obtenerCoordenadasSimuladas(entrega.getDireccionOrigen());
            LatLng destino = obtenerCoordenadasSimuladas(entrega.getDireccionDestino());

            if (mMap != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(origen).title("Origen: " + entrega.getDireccionOrigen()));
                mMap.addMarker(new MarkerOptions().position(destino).title("Destino: " + entrega.getDireccionDestino()));
                mMap.addPolyline(new PolylineOptions().add(origen).add(destino).color(0xFF0000FF).width(10));
                LatLng centroMapa = new LatLng((origen.latitude + destino.latitude) / 2, (origen.longitude + destino.longitude) / 2);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, 15));
            }

            btnAceptarEntrega.setVisibility(View.VISIBLE);
            btnRechazarEntrega.setVisibility(View.VISIBLE);

        } else {
            Log.d("ListaEntregasFragment", "No hay más entregas para mostrar.");
            textViewOrigenCard.setText("No hay más entregas disponibles.");
            textViewDestinoCard.setText("");
            textViewContenidoCard.setText("");
            if (mMap != null) mMap.clear();
            btnAceptarEntrega.setVisibility(View.GONE);
            btnRechazarEntrega.setVisibility(View.GONE);
        }
    }

    private void pasarASiguienteEntrega() {
        indiceEntregaActual++;
        mostrarEntregaActual();
    }

    private LatLng obtenerCoordenadasSimuladas(String direccion) {
        if (direccion.contains("Calle Falsa")) return new LatLng(-34.6037, -58.3816); // Obelisco
        if (direccion.contains("Avenida Siempreviva")) return new LatLng(-34.6179, -58.3715); // Plaza de Mayo
        if (direccion.contains("Diagonal Sur")) return new LatLng(-34.6075, -58.3733); // Casa Rosada
        if (direccion.contains("Ruta 66")) return new LatLng(35.2270, -101.9922); // Amarillo, TX (ejemplo lejano)
        if (direccion.contains("Corrientes 348")) return new LatLng(-34.603722, -58.387057); // Obelisco aprox
        if (direccion.contains("Lavalle 678")) return new LatLng(-34.6023, -58.3739); // Microcentro aprox
        return new LatLng(0, 0);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (listaEntregas != null && !listaEntregas.isEmpty()) {
            Entrega primeraEntrega = listaEntregas.get(0);
            LatLng origen = obtenerCoordenadasSimuladas(primeraEntrega.getDireccionOrigen());
            LatLng destino = obtenerCoordenadasSimuladas(primeraEntrega.getDireccionDestino());
            LatLng centroMapa = new LatLng((origen.latitude + destino.latitude) / 2, (origen.longitude + destino.longitude) / 2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, 12));
        }
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
    public void onDestroy() {
        super.onDestroy();
        mapViewEntregaCard.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewEntregaCard.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapViewEntregaCard.onSaveInstanceState(outState);
    }
}