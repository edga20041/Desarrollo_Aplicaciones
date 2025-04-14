package com.example.desarrollo_aplicaciones.api.model;

import com.example.desarrollo_aplicaciones.entity.Ruta;
import com.google.gson.annotations.SerializedName;

public class RutaAsignadaResponse {
    @SerializedName("id")
    private Long id;
    @SerializedName("ruta")
    private Ruta ruta; // Campo para la información de la ruta
    @SerializedName("repartidorId")
    private Long repartidorId;
    @SerializedName("estado")
    private String estado;
    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Long getRepartidorId() {
        return repartidorId;
    }

    public void setRepartidorId(Long repartidorId) {
        this.repartidorId = repartidorId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}