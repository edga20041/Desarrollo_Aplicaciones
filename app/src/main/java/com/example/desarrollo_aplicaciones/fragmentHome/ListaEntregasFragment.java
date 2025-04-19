package com.example.desarrollo_aplicaciones.fragmentHome;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.desarrollo_aplicaciones.repository.auth.EntregaAdapter;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ListaEntregasFragment extends Fragment {

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    private RecyclerView recyclerEntregas;
    private EntregaAdapter adapter;
    private List<Entrega> entregas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_entregas, container, false);
        recyclerEntregas = view.findViewById(R.id.recyclerEntregas);
        recyclerEntregas.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarEntregas();
        return view;
    }

    private void cargarEntregas() {
        String token = tokenRepository.getToken();
        Call<List<Entrega>> call = apiService.getEntregasPendientes("Bearer " + token);
        call.enqueue(new Callback<List<Entrega>>() {
            @Override
            public void onResponse(@NonNull Call<List<Entrega>> call, @NonNull Response<List<Entrega>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    entregas = response.body();
                    adapter = new EntregaAdapter(entregas, entrega -> {
                        Intent intent = new Intent(getContext(), DetalleEntregaActivity.class);
                        intent.putExtra("entrega_id", entrega.getId());
                        startActivity(intent);
                    });
                    recyclerEntregas.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Entrega>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error al cargar entregas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}