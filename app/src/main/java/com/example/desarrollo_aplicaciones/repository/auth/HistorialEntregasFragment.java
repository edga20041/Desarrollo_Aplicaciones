package com.example.desarrollo_aplicaciones.repository.auth;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.AuthApi;
import com.example.desarrollo_aplicaciones.entity.Entrega;

import dagger.hilt.android.AndroidEntryPoint;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@AndroidEntryPoint
public class HistorialEntregasFragment extends Fragment {

    private RecyclerView recyclerViewHistorial;
    private HistorialEntregaAdapter adapter;
    private List<Entrega> listaEntregas = new ArrayList<>();
    private TextView textViewEmptyHistory;

    @Inject
    AuthApi authApi; // Inyecta la interfaz AuthApi

    @Inject
    TokenRepository tokenRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_entregas, container, false);
        recyclerViewHistorial = view.findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        textViewEmptyHistory = view.findViewById(R.id.textViewEmptyHistory);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarHistorialEntregas(); // Llama al método para obtener los datos
    }

    private void cargarHistorialEntregas() {
        String token = tokenRepository.getToken();
        if (token != null && !token.isEmpty()) {
            Call<List<Entrega>> call = authApi.getHistorialEntregas("Bearer " + token);
            call.enqueue(new Callback<List<Entrega>>() {
                @Override
                public void onResponse(Call<List<Entrega>> call, Response<List<Entrega>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        listaEntregas = response.body();
                        adapter = new HistorialEntregaAdapter(listaEntregas);
                        recyclerViewHistorial.setAdapter(adapter);
                        actualizarVisibilidad();
                    } else {
                        // Manejar error de la respuesta (por ejemplo, mostrar un mensaje al usuario)
                        textViewEmptyHistory.setText("Error al cargar el historial.");
                        textViewEmptyHistory.setVisibility(View.VISIBLE);
                        recyclerViewHistorial.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<List<Entrega>> call, Throwable t) {
                    // Manejar error de conexión
                    textViewEmptyHistory.setText("Error de conexión.");
                    textViewEmptyHistory.setVisibility(View.VISIBLE);
                    recyclerViewHistorial.setVisibility(View.GONE);
                }
            });
        } else {
            // Manejar el caso en que no hay token (posiblemente redirigir al login)
            textViewEmptyHistory.setText("No se encontró el token de autenticación.");
            textViewEmptyHistory.setVisibility(View.VISIBLE);
            recyclerViewHistorial.setVisibility(View.GONE);
        }
    }

    private void actualizarVisibilidad() {
        if (listaEntregas.isEmpty()) {
            recyclerViewHistorial.setVisibility(View.GONE);
            textViewEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            recyclerViewHistorial.setVisibility(View.VISIBLE);
            textViewEmptyHistory.setVisibility(View.GONE);
        }
    }
}