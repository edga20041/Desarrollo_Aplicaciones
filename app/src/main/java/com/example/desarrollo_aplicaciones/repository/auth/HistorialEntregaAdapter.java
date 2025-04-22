package com.example.desarrollo_aplicaciones.repository.auth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.api.model.ApiService;
import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.example.desarrollo_aplicaciones.entity.Estado;
import com.example.desarrollo_aplicaciones.repository.auth.DetalleEntregaHistorialActivity;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistorialEntregaAdapter extends RecyclerView.Adapter<HistorialEntregaAdapter.ViewHolder> {

    private List<Entrega> listaEntregas;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Context context;
    private TokenRepository tokenRepository;

    public HistorialEntregaAdapter(List<Entrega> listaEntregas, Context context, TokenRepository tokenRepository) {
        this.listaEntregas = listaEntregas;
        this.context = context;
        this.tokenRepository = tokenRepository;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_entrega, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listaEntregas != null && position < listaEntregas.size()) {
            Entrega entrega = listaEntregas.get(position);
            holder.textViewCliente.setText("Cliente: " + entrega.getCliente());
            holder.textViewProducto.setText("Producto: " + entrega.getProducto());

            obtenerEstadoDeEntrega(entrega.getEstadoId(), holder);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetalleEntregaHistorialActivity.class);
                intent.putExtra("entrega_id", entrega.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaEntregas != null ? listaEntregas.size() : 0;
    }

    private void obtenerEstadoDeEntrega(Long estadoId, ViewHolder holder) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Estado> call = apiService.getEstado(estadoId, "Bearer " + tokenRepository.getToken()); // Necesitas el token aquí
        call.enqueue(new Callback<Estado>() {
            @Override
            public void onResponse(Call<Estado> call, Response<Estado> response) {
                if (response.isSuccessful()) {
                    Estado estado = response.body();
                    if (estado != null) {
                        holder.textViewEstadoFinal.setText("Estado: " + estado.getNombre());
                    } else {
                        holder.textViewEstadoFinal.setText("Estado desconocido");
                    }
                } else {
                    holder.textViewEstadoFinal.setText("Error al obtener estado");
                }
            }

            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                holder.textViewEstadoFinal.setText("Error de conexión");
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCliente;
        public TextView textViewProducto;
        public TextView textViewEstadoFinal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCliente = itemView.findViewById(R.id.textViewCliente);
            textViewProducto = itemView.findViewById(R.id.textViewProducto);
            textViewEstadoFinal = itemView.findViewById(R.id.textViewEstadoFinal);
        }
    }
}