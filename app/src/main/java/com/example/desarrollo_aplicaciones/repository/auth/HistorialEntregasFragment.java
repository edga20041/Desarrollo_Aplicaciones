package com.example.desarrollo_aplicaciones.repository.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@AndroidEntryPoint
public class HistorialEntregasFragment extends Fragment {

    @Inject
    TokenRepository tokenRepository;
    @Inject
    ApiService apiService;

    private RecyclerView recyclerViewHistorial;
    private HistorialEntregaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_entregas, container, false);
        recyclerViewHistorial = view.findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarHistorial();

        return view;
    }

    private void cargarHistorial() {
        String token = tokenRepository.getToken();

        if (token != null) {
            Call<List<Entrega>> call = apiService.getHistorialEntregas("Bearer " + token);
            call.enqueue(new Callback<List<Entrega>>() {
                @Override
                public void onResponse(Call<List<Entrega>> call, Response<List<Entrega>> response) {
                    if (response.isSuccessful()) {
                        List<Entrega> listaEntregas = response.body();
                        if (listaEntregas != null && !listaEntregas.isEmpty()) {
                            adapter = new HistorialEntregaAdapter(listaEntregas, getContext(), tokenRepository);
                            recyclerViewHistorial.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "No hay entregas disponibles.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al cargar el historial", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Entrega>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
        }
    }
}