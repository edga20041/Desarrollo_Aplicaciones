package com.example.desarrollo_aplicaciones.repository.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Entrega;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HistorialEntregasFragment extends Fragment {

    private static final String TAG = "HistorialFragment";

    @Inject
    TokenRepository tokenRepository;
    @Inject
    ApiService apiService;

    private RecyclerView recyclerViewHistorial;
    private HistorialEntregaAdapter adapter;
    private TextView textViewEmptyHistory;
    private List<Entrega> historialEntregas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflando layout");
        View view = inflater.inflate(R.layout.fragment_historial_entregas, container, false);
        recyclerViewHistorial = view.findViewById(R.id.recyclerViewHistorial);
        textViewEmptyHistory = view.findViewById(R.id.textViewEmptyHistory);

        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistorialEntregaAdapter(historialEntregas, getContext(), tokenRepository);
        recyclerViewHistorial.setAdapter(adapter);

        Log.d(TAG, "onCreateView: Layout inflado y RecyclerView configurado");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Cargando historial");
        cargarHistorial();
    }

    private void cargarHistorial() {
        Log.d(TAG, "cargarHistorial: Iniciando carga");
        recyclerViewHistorial.setVisibility(View.GONE);
        textViewEmptyHistory.setVisibility(View.VISIBLE);
        textViewEmptyHistory.setText("Cargando historial...");

        String token = tokenRepository.getToken();
        if (token != null) {
            Log.d(TAG, "Token encontrado, realizando llamada API");
            Call<List<Entrega>> call = apiService.getHistorialEntregas("Bearer " + token);
            call.enqueue(new Callback<List<Entrega>>() {
                @Override
                public void onResponse(@NonNull Call<List<Entrega>> call, @NonNull Response<List<Entrega>> response) {
                    Log.d(TAG, "onResponse: Código HTTP: " + response.code());
                    if (response.isSuccessful()) {
                        List<Entrega> listaDesdeApi = response.body();
                        Log.d(TAG, "onResponse: Respuesta API OK. Tamaño lista: " + (listaDesdeApi == null ? 0 : listaDesdeApi.size()));
                        if (listaDesdeApi != null && !listaDesdeApi.isEmpty()) {
                            Log.d(TAG, "onResponse: Historial encontrado, actualizando adapter");
                            historialEntregas.clear();
                            historialEntregas.addAll(listaDesdeApi);
                            adapter.notifyDataSetChanged();
                            recyclerViewHistorial.setVisibility(View.VISIBLE);
                            textViewEmptyHistory.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "onResponse: Lista de historial vacía o nula.");
                            recyclerViewHistorial.setVisibility(View.GONE);
                            textViewEmptyHistory.setVisibility(View.VISIBLE);
                            textViewEmptyHistory.setText("No hay historial de entregas.");
                        }
                    } else {
                        Log.e(TAG, "onResponse: Error API: " + response.code() + " - " + response.message());
                        recyclerViewHistorial.setVisibility(View.GONE);
                        textViewEmptyHistory.setVisibility(View.VISIBLE);
                        textViewEmptyHistory.setText("Error al cargar el historial (Cod: " + response.code() + ")");
                        Toast.makeText(getContext(), "Error al cargar el historial", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Entrega>> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: Error de conexión al cargar historial", t);
                    recyclerViewHistorial.setVisibility(View.GONE);
                    textViewEmptyHistory.setVisibility(View.VISIBLE);
                    textViewEmptyHistory.setText("Error de conexión al cargar el historial.");
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w(TAG, "cargarHistorial: Token no encontrado");
            recyclerViewHistorial.setVisibility(View.GONE);
            textViewEmptyHistory.setVisibility(View.VISIBLE);
            textViewEmptyHistory.setText("Error de autenticación.");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }
}