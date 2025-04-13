package com.example.desarrollo_aplicaciones.entity;

public class Entrega {
    private String tiempoEntrega;
    private String cliente;
    private String estadoFinal;
    private String direccionOrigen; // Nuevo campo
    private String direccionDestino; // Nuevo campo
    private String contenido; // Nuevo campo

    // Constructor modificado para incluir los nuevos campos (opcional, depende de cómo creas las Entregas)
    public Entrega(String tiempoEntrega, String cliente, String estadoFinal, String direccionOrigen, String direccionDestino, String contenido) {
        this.tiempoEntrega = tiempoEntrega;
        this.cliente = cliente;
        this.estadoFinal = estadoFinal;
        this.direccionOrigen = direccionOrigen;
        this.direccionDestino = direccionDestino;
        this.contenido = contenido;
    }

    // Constructor sin los nuevos campos (si ya tienes código que crea Entregas así)
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

    public String getDireccionOrigen() { // Getter para direccionOrigen
        return direccionOrigen;
    }

    public String getDireccionDestino() { // Getter para direccionDestino
        return direccionDestino;
    }

    public String getContenido() { // Getter para contenido
        return contenido;
    }

    // Setters (opcional, si necesitas modificar estos campos después de la creación)
    public void setDireccionOrigen(String direccionOrigen) {
        this.direccionOrigen = direccionOrigen;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}