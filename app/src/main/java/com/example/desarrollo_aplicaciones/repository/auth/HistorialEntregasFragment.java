package com.example.desarrollo_aplicaciones.repository.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Entrega;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HistorialEntregasFragment extends Fragment {

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    private RecyclerView recyclerViewHistorial;
    private HistorialEntregaAdapter adapter;
    private List<Entrega> listaHistorialEntregas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_entregas, container, false);
        recyclerViewHistorial = view.findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarHistorial();
    }

    private void cargarHistorial() {
        String token = tokenRepository.getToken();
        if (token != null) {
            Call<List<Entrega>> call = apiService.getHistorialEntregas("Bearer " + token);
            call.enqueue(new Callback<List<Entrega>>() {
                @Override
                public void onResponse(@NonNull Call<List<Entrega>> call, @NonNull Response<List<Entrega>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        listaHistorialEntregas = response.body();
                        adapter = new HistorialEntregaAdapter(listaHistorialEntregas);
                        recyclerViewHistorial.setAdapter(adapter);
                    } else {
                        Log.e("HistorialFragment", "Error al cargar el historial: " + response.code());
                        Toast.makeText(getContext(), "Error al cargar el historial", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Entrega>> call, @NonNull Throwable t) {
                    Log.e("HistorialFragment", "Error de conexión al cargar el historial: " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("HistorialFragment", "Token no encontrado.");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }
}