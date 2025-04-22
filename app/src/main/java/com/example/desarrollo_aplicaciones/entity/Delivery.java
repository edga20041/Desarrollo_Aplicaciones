package com.example.desarrollo_aplicaciones.entity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Delivery {
    private Long deliveryTimeMillis; // Cambiado el nombre para claridad
    private String clientName;
    private String status;
    private String deliveryManEmail;

    public Delivery() {
    }

    public Delivery(Long deliveryTimeMillis, String clientName, String status, String deliveryManEmail) {
        this.deliveryTimeMillis = deliveryTimeMillis;
        this.clientName = clientName;
        this.status = status;
        this.deliveryManEmail = deliveryManEmail;
    }

    public Long getDeliveryTimeMillis() {
        return deliveryTimeMillis;
    }

    public String getFormattedDeliveryTime() {
        if (deliveryTimeMillis != null) {
            Date date = new Date(deliveryTimeMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
        return null;
    }

    public String getClientName() {
        return clientName;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryManEmail() {
        return deliveryManEmail;
    }

    public void setDeliveryTimeMillis(Long deliveryTimeMillis) {
        this.deliveryTimeMillis = deliveryTimeMillis;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeliveryManEmail(String deliveryManEmail) {
        this.deliveryManEmail = deliveryManEmail;
    }
}