package com.example.desarrollo_aplicaciones.entity;

public class Entrega {
    private Long id;
    private String cliente;
    private Integer clienteDni;
    private Long estadoId;
    private Long repartidorId;
    private Long rutaId;
    private String producto;
    private String fechaCreacion;
    private String fechaAsignacion;
    private String fechaFinalizacion;

    public Entrega() {
    }

    public Entrega(Long id, String cliente, Integer clienteDni, Long estadoId, Long repartidorId, Long rutaId, String producto, String fechaCreacion, String fechaAsignacion, String fechaFinalizacion) {
        this.id = id;
        this.cliente = cliente;
        this.clienteDni = clienteDni;
        this.estadoId = estadoId;
        this.repartidorId = repartidorId;
        this.rutaId = rutaId;
        this.producto = producto;
        this.fechaCreacion = fechaCreacion;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Entrega(Long id, String cliente, String producto, Long estadoId) {
        this.id = id;
        this.cliente = cliente;
        this.producto = producto;
        this.estadoId = estadoId;
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

    public Integer getClienteDni() {
        return clienteDni;
    }

    public void setClienteDni(Integer clienteDni) {
        this.clienteDni = clienteDni;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    public Long getRepartidorId() {
        return repartidorId;
    }

    public void setRepartidorId(Long repartidorId) {
        this.repartidorId = repartidorId;
    }

    public Long getRutaId() {
        return rutaId;
    }

    public void setRutaId(Long rutaId) {
        this.rutaId = rutaId;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(String fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }
}
