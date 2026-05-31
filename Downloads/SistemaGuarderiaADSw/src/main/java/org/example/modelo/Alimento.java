package org.example.modelo;

public class Alimento {

    private String nombre;
    private String cantidad;
    private boolean permitido;

    public Alimento(String nombre, String cantidad, boolean permitido) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.permitido = permitido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public boolean isPermitido() {
        return permitido;
    }
}