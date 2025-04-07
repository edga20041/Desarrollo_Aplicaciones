package com.example.desarrollo_aplicaciones.repository.auth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_aplicaciones.R;
import com.example.desarrollo_aplicaciones.entity.Delivery; // Asegúrate de que esta importación sea correcta

import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    private List<Delivery> deliveryList;

    public DeliveryAdapter(List<Delivery> deliveryList) {
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        Delivery delivery = deliveryList.get(position);
        holder.deliveryTimeTextView.setText("Tiempo: " + delivery.getFormattedDeliveryTime());
        holder.clientNameTextView.setText("Cliente: " + delivery.getClientName());
        holder.deliveryStatusTextView.setText("Estado: " + delivery.getStatus());
    }
    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        public TextView deliveryTimeTextView;
        public TextView clientNameTextView;
        public TextView deliveryStatusTextView;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryTimeTextView = itemView.findViewById(R.id.deliveryTimeTextView);
            clientNameTextView = itemView.findViewById(R.id.clientNameTextView);
            deliveryStatusTextView = itemView.findViewById(R.id.deliveryStatusTextView);
        }
    }

    // Método para actualizar la lista de entregas (opcional)
    public void updateDeliveries(List<Delivery> newDeliveryList) {
        this.deliveryList = newDeliveryList;
        notifyDataSetChanged();
    }
}