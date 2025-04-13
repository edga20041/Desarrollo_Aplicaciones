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
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListaEntregasFragment extends Fragment implements OnMapReadyCallback {

    private List<Entrega> listaEntregas;
    private int indiceEntregaActual = 0;

    private MapView mapViewEntregaCard;
    private GoogleMap mMap;
    private TextView textViewOrigenCard;
    private TextView textViewDestinoCard;
    private TextView textViewContenidoCard;
    private Button btnRechazarEntrega;
    private Button btnAceptarEntrega;
    private Polyline rutaPolyline;

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
        entregas.add(new Entrega("11:15", "Carlos López", "Entregado", "Corrientes 348, Buenos Aires", "Lavalle 678, Buenos Aires", "Flores"));
        return entregas;
    }

    private void mostrarEntregaActual() {
        if (listaEntregas != null && !listaEntregas.isEmpty() && indiceEntregaActual < listaEntregas.size()) {
            Entrega entrega = listaEntregas.get(indiceEntregaActual);
            textViewOrigenCard.setText("Origen: " + entrega.getDireccionOrigen());
            textViewDestinoCard.setText("Destino: " + entrega.getDireccionDestino());
            textViewContenidoCard.setText("Contenido: " + entrega.getContenido());

            LatLng origen = obtenerCoordenadasSimuladas(entrega.getDireccionOrigen());
            LatLng destino = obtenerCoordenadasSimuladas(entrega.getDireccionDestino());

            if (mMap != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(origen).title("Origen"));
                mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));
                solicitarRuta(origen, destino);

                LatLng centroMapa = new LatLng(
                        (origen.latitude + destino.latitude) / 2,
                        (origen.longitude + destino.longitude) / 2
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, 14));
            }

            btnAceptarEntrega.setVisibility(View.VISIBLE);
            btnRechazarEntrega.setVisibility(View.VISIBLE);
        } else {
            textViewOrigenCard.setText("No hay más entregas disponibles.");
            textViewDestinoCard.setText("");
            textViewContenidoCard.setText("");
            if (mMap != null) mMap.clear();
            btnAceptarEntrega.setVisibility(View.GONE);
            btnRechazarEntrega.setVisibility(View.GONE);
        }
    }

    private void solicitarRuta(LatLng origen, LatLng destino) {
        String origenStr = origen.latitude + "," + origen.longitude;
        String destinoStr = destino.latitude + "," + destino.longitude;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origenStr +
                "&destination=" + destinoStr +
                "&mode=driving" +
                "&key=AIzaSyDFT0PTre5bG8loS6X2K9ujVJ-AkeRxZgM";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Ruta", "Error en la solicitud: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject polyline = route.getJSONObject("overview_polyline");
                            String points = polyline.getString("points");

                            List<LatLng> decodedPath = decodePoly(points);
                            Log.d("Ruta", "Cantidad de puntos: " + decodedPath.size());

                            requireActivity().runOnUiThread(() -> {
                                if (mMap != null && !decodedPath.isEmpty()) {
                                    if (rutaPolyline != null) {
                                        rutaPolyline.remove();
                                    }

                                    // Asegurarse de que la polilínea se está dibujando correctamente
                                    rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                            .addAll(decodedPath)
                                            .color(android.graphics.Color.BLUE)
                                            .width(12f));

                                    // Log para verificar si la ruta se está mostrando en el mapa
                                    Log.d("Ruta", "Polilínea añadida al mapa");
                                }
                            });
                        } else {
                            Log.e("Ruta", "No se encontraron rutas");
                        }
                    } catch (JSONException e) {
                        Log.e("Ruta", "Error al parsear JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("Ruta", "Respuesta no exitosa");
                }
            }
        });
    }



    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new LatLng(lat / 1E5, lng / 1E5));
        }

        return poly;
    }

    private void pasarASiguienteEntrega() {
        indiceEntregaActual++;
        mostrarEntregaActual();
    }

    private LatLng obtenerCoordenadasSimuladas(String direccion) {
        if (direccion.contains("Corrientes 348")) return new LatLng(-34.603722, -58.387057);
        if (direccion.contains("Lavalle 678")) return new LatLng(-34.6023, -58.3739);
        return new LatLng(0, 0);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mostrarEntregaActual();
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
