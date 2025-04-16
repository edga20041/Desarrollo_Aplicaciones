package com.example.desarrollo_aplicaciones.api.model;

public class EntregaResponse {
    private Long id;
    private String cliente;
    private String estadoFinal;
    private String fechaFinalizacion;

    // Getters
    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getEstadoFinal() { return estadoFinal; }
    public String getFechaFinalizacion() { return fechaFinalizacion; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public void setEstadoFinal(String estadoFinal) { this.estadoFinal = estadoFinal; }
    public void setFechaFinalizacion(String fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
}