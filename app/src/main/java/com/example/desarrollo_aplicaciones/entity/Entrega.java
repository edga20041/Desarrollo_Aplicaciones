package com.example.desarrollo_aplicaciones.entity;

public class Entrega {
    private Long id;
    private String cliente;
    private String estadoFinal;
    private String fechaFinalizacion;

    public Entrega() {
    }

    public Entrega(Long id, String cliente, String estadoFinal, String fechaFinalizacion) {
        this.id = id;
        this.cliente = cliente;
        this.estadoFinal = estadoFinal;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(String fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }
}