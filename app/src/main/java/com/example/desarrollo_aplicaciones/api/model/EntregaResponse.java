package com.example.desarrollo_aplicaciones.api.model;

public class EntregaResponse {
    private Long id;
    private String tiempoEntrega;
    private String cliente;
    private String estadoFinal;
    private Boolean aceptada;
    private String tiempoDecision;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(String tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public void setAceptada(Boolean aceptada) {
        this.aceptada = aceptada;
    }

    public String getTiempoDecision() {
        return tiempoDecision;
    }

    public void setTiempoDecision(String tiempoDecision) {
        this.tiempoDecision = tiempoDecision;
    }
}
