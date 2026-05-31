package org.example.modelo;

import java.io.Serializable;

public class Dieta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String descripcion;

    public Dieta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}