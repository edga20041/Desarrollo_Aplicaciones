package com.example.desarrollo_aplicaciones.fragmentHome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.example.desarrollo_aplicaciones.repository.auth.EntregaAdapter;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ListaEntregasFragment extends Fragment {

    private static final String TAG = "ListaEntregasFragment";

    @Inject
    ApiService apiService;
    @Inject
    TokenRepository tokenRepository;

    private RecyclerView recyclerEntregas;
    private EntregaAdapter adapter;
    private List<Entrega> entregas = new ArrayList<>();
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver entregaFinalizadaReceiver;
    private TextView textViewEmptyList;
    private LinearLayout entregasLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflando layout");
        View view = inflater.inflate(R.layout.fragment_lista_entregas, container, false);
        recyclerEntregas = view.findViewById(R.id.recyclerEntregas);
        textViewEmptyList = view.findViewById(R.id.textViewEmptyList);
        entregasLayout = view.findViewById(R.id.entregasLayout);

        recyclerEntregas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntregaAdapter(entregas, entrega -> {
            Intent intent = new Intent(getContext(), DetalleEntregaActivity.class);
            intent.putExtra("entrega_id", entrega.getId());
            startActivity(intent);
        }, getContext(), apiService, tokenRepository);
        recyclerEntregas.setAdapter(adapter);

        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext());
        Log.d(TAG, "onCreateView: Layout inflado y RecyclerView configurado");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Iniciando carga de entregas");
        cargarEntregas();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Registrando BroadcastReceiver");
        registrarBroadcastReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Recargando entregas");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Desregistrando BroadcastReceiver");
        unregisterBroadcastReceiver();
    }

    private void registrarBroadcastReceiver() {
        entregaFinalizadaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Broadcast recibido: " + intent.getAction());
                if ("entrega_finalizada".equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Acción 'entrega_finalizada', recargando entregas.");
                    cargarEntregas();
                }
            }
        };
        localBroadcastManager.registerReceiver(entregaFinalizadaReceiver, new IntentFilter("entrega_finalizada"));
        Log.d(TAG, "BroadcastReceiver registrado para 'entrega_finalizada'");
    }

    private void unregisterBroadcastReceiver() {
        if (entregaFinalizadaReceiver != null) {
            localBroadcastManager.unregisterReceiver(entregaFinalizadaReceiver);
            Log.d(TAG, "BroadcastReceiver desregistrado.");
        }
    }

    private void cargarEntregas() {
        Log.d(TAG, "cargarEntregas: Iniciando carga");
        entregasLayout.setVisibility(View.GONE);
        textViewEmptyList.setVisibility(View.VISIBLE);
        textViewEmptyList.setText("Cargando entregas pendientes...");

        String token = tokenRepository.getToken();
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "cargarEntregas: Token no encontrado");
            entregasLayout.setVisibility(View.GONE);
            textViewEmptyList.setVisibility(View.VISIBLE);
            textViewEmptyList.setText("Error de autenticación.");
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Token encontrado, realizando llamada API para entregas pendientes");
        Call<List<Entrega>> call = apiService.getEntregasPendientes("Bearer " + token);
        call.enqueue(new Callback<List<Entrega>>() {
            @Override
            public void onResponse(@NonNull Call<List<Entrega>> call, @NonNull Response<List<Entrega>> response) {
                Log.d(TAG, "onResponse: Código HTTP: " + response.code());
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "onResponse: Fragmento no adjunto, ignorando respuesta.");
                    return;
                }

                if (response.isSuccessful()) {
                    List<Entrega> nuevasEntregas = response.body();
                    Log.d(TAG, "onResponse: Respuesta API OK. Tamaño lista: " + (nuevasEntregas == null ? 0 : nuevasEntregas.size()));
                    if (nuevasEntregas != null && !nuevasEntregas.isEmpty()) {
                        Log.d(TAG, "onResponse: Entregas encontradas, actualizando adapter");
                        getActivity().runOnUiThread(() -> {
                            entregas.clear();
                            entregas.addAll(nuevasEntregas);
                            adapter.notifyDataSetChanged();
                            entregasLayout.setVisibility(View.VISIBLE);
                            textViewEmptyList.setVisibility(View.GONE);
                        });
                    } else {
                        Log.d(TAG, "onResponse: Lista de entregas vacía o nula.");
                        getActivity().runOnUiThread(() -> {
                            entregasLayout.setVisibility(View.GONE);
                            textViewEmptyList.setVisibility(View.VISIBLE);
                            textViewEmptyList.setText("No hay entregas pendientes.");
                        });
                    }
                } else {
                    Log.e(TAG, "onResponse: Error API: " + response.code() + " - " + response.message());
                    getActivity().runOnUiThread(() -> {
                        entregasLayout.setVisibility(View.GONE);
                        textViewEmptyList.setVisibility(View.VISIBLE);
                        textViewEmptyList.setText("Error al cargar entregas (Cod: " + response.code() + ")");
                    });
                    Toast.makeText(getContext(), "Error al cargar entregas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Entrega>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "onFailure: Fragmento no adjunto, ignorando error.");
                    return;
                }
                Log.e(TAG, "onFailure: Error de conexión al cargar entregas", t);
                getActivity().runOnUiThread(() -> {
                    entregasLayout.setVisibility(View.GONE);
                    textViewEmptyList.setVisibility(View.VISIBLE);
                    textViewEmptyList.setText("Error de conexión al cargar entregas.");
                });
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}