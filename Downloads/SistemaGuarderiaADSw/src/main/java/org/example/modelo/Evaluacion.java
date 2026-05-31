package org.example.modelo;

import java.io.Serializable;

public class Evaluacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String empleado;
    private String cargo;
    private String calificacion;

    public Evaluacion(String empleado, String cargo, String calificacion) {
        this.empleado = empleado;
        this.cargo = cargo;
        this.calificacion = calificacion;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getCargo() {
        return cargo;
    }

    public String getCalificacion() {
        return calificacion;
    }
}