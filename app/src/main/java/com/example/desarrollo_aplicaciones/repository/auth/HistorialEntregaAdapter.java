/*package com.example.desarrollo_aplicaciones.repository.auth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.entity.Entrega;

import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Locale;

public class HistorialEntregaAdapter extends RecyclerView.Adapter<HistorialEntregaAdapter.ViewHolder> {

    private List<Entrega> listaEntregas;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public HistorialEntregaAdapter(List<Entrega> listaEntregas) {
        Log.d("HistorialAdapter", "HistorialEntregaAdapter creado con lista de tamaño: " + (listaEntregas != null ? listaEntregas.size() : "null"));
        this.listaEntregas = listaEntregas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("HistorialAdapter", "onCreateViewHolder() llamado.");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_entrega, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("HistorialAdapter", "onBindViewHolder() llamado para posición: " + position);
        if (listaEntregas != null && position < listaEntregas.size()) {
            Entrega entrega = listaEntregas.get(position);
            Log.d("HistorialAdapter", "Entrega en posición " + position + ": Cliente=" + entrega.getCliente() +
                    ", EstadoFinal=" + entrega.getEstadoFinal() +
                    ", FechaFinalizacion=" + entrega.getFechaFinalizacion());
            holder.textViewCliente.setText("Cliente: " + entrega.getCliente());
            holder.textViewEstadoFinal.setText("Estado: " + entrega.getEstadoFinal());
            if (entrega.getFechaFinalizacion() != null) {
                try {
                    LocalDateTime fecha = LocalDateTime.parse(entrega.getFechaFinalizacion());
                    holder.textViewFechaFinalizacion.setText("Fecha: " + fecha.format(dateFormatter));
                } catch (Exception e) {
                    Log.e("HistorialAdapter", "Error al formatear la fecha: " + entrega.getFechaFinalizacion(), e);
                    holder.textViewFechaFinalizacion.setText("Fecha: " + entrega.getFechaFinalizacion());
                }
            } else {
                holder.textViewFechaFinalizacion.setText("Fecha: N/A");
            }
        } else {
            Log.w("HistorialAdapter", "La lista de entregas es nula o la posición está fuera de rango.");
            holder.textViewCliente.setText("Cliente: N/A");
            holder.textViewEstadoFinal.setText("Estado: N/A");
            holder.textViewFechaFinalizacion.setText("Fecha: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return listaEntregas != null ? listaEntregas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCliente;
        public TextView textViewEstadoFinal;
        public TextView textViewFechaFinalizacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCliente = itemView.findViewById(R.id.textViewCliente);
            textViewEstadoFinal = itemView.findViewById(R.id.textViewEstadoFinal);
            textViewFechaFinalizacion = itemView.findViewById(R.id.textViewFechaFinalizacion);
        }
    }
}*/