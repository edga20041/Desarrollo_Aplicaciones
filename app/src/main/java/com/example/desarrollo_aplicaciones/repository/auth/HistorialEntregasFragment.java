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
import com.example.desarrollo_aplicaciones.api.model.EntregaResponse;
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

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

    private RecyclerView recyclerViewHistorial;
    private HistorialEntregaAdapter adapter;
    private List<Entrega> listaHistorialEntregas;
    private ApiService apiServiceLocal; // Nueva instancia local

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String BASE_URL = "http://10.0.2.2:8081/"; // Asegúrate de que sea la correcta
        apiServiceLocal = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HistorialFragment", "onCreateView() llamado.");
        View view = inflater.inflate(R.layout.fragment_historial_entregas, container, false);
        recyclerViewHistorial = view.findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("HistorialFragment", "onViewCreated() llamado.");
        cargarHistorial();
    }

    private void cargarHistorial() {
        String token = tokenRepository.getToken();
        Log.d("HistorialFragment", "cargarHistorial() llamado. Token: " + token);
        if (token != null) {
            Call<List<EntregaResponse>> call = apiServiceLocal.getHistorialEntregas("Bearer " + token); // Usa la instancia local
            call.enqueue(new Callback<List<EntregaResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<EntregaResponse>> call, @NonNull Response<List<EntregaResponse>> response) {
                    Log.d("HistorialFragment", "onResponse() llamado. Código: " + response.code());

                    if (response.isSuccessful()) {
                        Log.d("HistorialFragment", "Respuesta exitosa.");
                        if (response.body() != null) {
                            Log.d("HistorialFragment", "Cuerpo de la respuesta (toString): " + response.body().toString());

                            Gson gson = new Gson();
                            try {
                                String responseBodyString = gson.toJson(response.body());
                                Log.d("HistorialFragment", "Cuerpo de la respuesta (Gson.toJson): " + responseBodyString);

                                List<EntregaResponse> listaHistorialEntregasResponse = gson.fromJson(responseBodyString, new TypeToken<List<EntregaResponse>>() {}.getType());
                                Log.d("HistorialFragment", "Número de entregas recibidas (después de Gson): " + listaHistorialEntregasResponse.size());

                                // Convierte la lista de EntregaResponse a una lista de Entrega
                                List<Entrega> listaHistorialEntregas = listaHistorialEntregasResponse.stream()
                                        .map(responseItem -> new Entrega(
                                                responseItem.getId(),
                                                responseItem.getCliente(),
                                                responseItem.getEstadoFinal(),
                                                responseItem.getFechaFinalizacion()
                                        ))
                                        .collect(Collectors.toList());

                                adapter = new HistorialEntregaAdapter(listaHistorialEntregas);
                                recyclerViewHistorial.setAdapter(adapter);

                            } catch (Exception e) {
                                Log.e("HistorialFragment", "Error al deserializar con Gson: " + e.getMessage(), e);
                                Toast.makeText(getContext(), "Error al procesar el historial", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.w("HistorialFragment", "El cuerpo de la respuesta es nulo.");
                            Toast.makeText(getContext(), "Error: El historial está vacío", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("HistorialFragment", "Error al cargar el historial: Código=" + response.code() + ", Mensaje=" + response.message());
                        try {
                            Log.e("HistorialFragment", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("HistorialFragment", "Error al leer el Error Body", e);
                        }
                        Toast.makeText(getContext(), "Error al cargar el historial", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<EntregaResponse>> call, @NonNull Throwable t) {
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