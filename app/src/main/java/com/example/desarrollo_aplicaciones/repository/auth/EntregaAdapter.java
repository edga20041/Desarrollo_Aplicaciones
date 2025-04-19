package com.example.desarrollo_aplicaciones.repository.auth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.entity.Entrega;

import java.util.List;

public class EntregaAdapter extends RecyclerView.Adapter<EntregaAdapter.EntregaViewHolder> {

    private List<Entrega> entregaList;
    private OnEntregaClickListener listener;

    public interface OnEntregaClickListener {
        void onEntregaClick(Entrega entrega);
    }

    public EntregaAdapter(List<Entrega> entregaList, OnEntregaClickListener listener) {
        this.entregaList = entregaList;
        this.listener = listener;
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
        holder.textEstado.setText("Estado ID: " + entrega.getEstadoId());

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
}
