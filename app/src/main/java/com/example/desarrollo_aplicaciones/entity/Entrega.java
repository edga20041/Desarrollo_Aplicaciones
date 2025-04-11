package com.example.desarrollo_aplicaciones.entity;

public class Entrega {
    private String tiempoEntrega;
    private String cliente;
    private String estadoFinal;

    public Entrega(String tiempoEntrega, String cliente, String estadoFinal) {
        this.tiempoEntrega = tiempoEntrega;
        this.cliente = cliente;
        this.estadoFinal = estadoFinal;
    }

    public String getTiempoEntrega() {
        return tiempoEntrega;
    }

    public String getCliente() {
        return cliente;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }
}