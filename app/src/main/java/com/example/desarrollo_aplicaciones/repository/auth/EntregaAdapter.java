package com.example.desarrollo_aplicaciones.repository.auth;

import android.content.Context;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntregaAdapter extends RecyclerView.Adapter<EntregaAdapter.EntregaViewHolder> {

    private List<Entrega> entregaList;
    private OnEntregaClickListener listener;
    private Context context;

    public interface OnEntregaClickListener {
        void onEntregaClick(Entrega entrega);
    }

    private ApiService apiService; // Agrega esta variable

    private TokenRepository tokenRepository; // Agrega esta variable

    public EntregaAdapter(List<Entrega> entregaList, OnEntregaClickListener listener, Context context, ApiService apiService, TokenRepository tokenRepository) {
        this.entregaList = entregaList;
        this.listener = listener;
        this.context = context;
        this.apiService = apiService;
        this.tokenRepository = tokenRepository;
    }


    @NonNull
    @Override
    public EntregaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrega, parent, false);
        return new EntregaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EntregaViewHolder holder, int position) {
        Entrega entrega = entregaList.get(position);
        holder.textId.setText("ID: " + entrega.getId());
        holder.textCliente.setText("Cliente: " + entrega.getCliente());
        holder.textProducto.setText("Producto: " + entrega.getProducto());

        // Obtener el nombre del estado por ID
        obtenerEstadoDeEntrega(entrega.getEstadoId(), holder);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEntregaClick(entrega);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entregaList.size();
    }

    public static class EntregaViewHolder extends RecyclerView.ViewHolder {
        public TextView textId;
        public TextView textCliente;
        public TextView textProducto;
        public TextView textEstado;

        public EntregaViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.textId);
            textCliente = itemView.findViewById(R.id.textCliente);
            textProducto = itemView.findViewById(R.id.textProducto);
            textEstado = itemView.findViewById(R.id.textEstado);
        }
    }

    public void updateEntregas(List<Entrega> nuevaLista) {
        this.entregaList = nuevaLista;
        notifyDataSetChanged();
    }

    private void obtenerEstadoDeEntrega(Long estadoId, EntregaViewHolder holder) {
        String token = tokenRepository.getToken(); // Asegúrate de tener acceso al TokenRepository en el Adapter
        Call<Estado> call = apiService.obtenerEstadoPorId(estadoId, "Bearer " + token); // Pasa el token

        call.enqueue(new Callback<Estado>() {
            @Override
            public void onResponse(Call<Estado> call, Response<Estado> response) {
                if (response.isSuccessful()) {
                    Estado estado = response.body();
                    if (estado != null) {
                        holder.textEstado.setText("Estado: " + estado.getNombre());
                    } else {
                        holder.textEstado.setText("Estado desconocido");
                    }
                } else {
                    holder.textEstado.setText("Error al obtener estado");
                }
            }

            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                holder.textEstado.setText("Error de conexión");
            }
        });
    }
}
