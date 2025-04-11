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

public class HistorialEntregaAdapter extends RecyclerView.Adapter<HistorialEntregaAdapter.ViewHolder> {

    private List<Entrega> listaEntregas;

    public HistorialEntregaAdapter(List<Entrega> listaEntregas) {
        this.listaEntregas = listaEntregas;
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
        Entrega entrega = listaEntregas.get(position);
        holder.textViewTiempoEntrega.setText("Tiempo: " + entrega.getTiempoEntrega());
        holder.textViewCliente.setText("Cliente: " + entrega.getCliente());
        holder.textViewEstadoFinal.setText("Estado: " + entrega.getEstadoFinal());
    }

    @Override
    public int getItemCount() {
        return listaEntregas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTiempoEntrega;
        public TextView textViewCliente;
        public TextView textViewEstadoFinal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTiempoEntrega = itemView.findViewById(R.id.textViewTiempoEntrega);
            textViewCliente = itemView.findViewById(R.id.textViewCliente);
            textViewEstadoFinal = itemView.findViewById(R.id.textViewEstadoFinal);
        }
    }
}