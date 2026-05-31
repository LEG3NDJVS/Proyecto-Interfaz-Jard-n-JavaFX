package org.example.modelo;

import java.io.Serializable;

public class Actividad implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String nino;
    private String empleado;

    public Actividad(String nombre, String nino, String empleado) {
        this.nombre = nombre;
        this.nino = nino;
        this.empleado = empleado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNino() {
        return nino;
    }

    public String getEmpleado() {
        return empleado;
    }
}