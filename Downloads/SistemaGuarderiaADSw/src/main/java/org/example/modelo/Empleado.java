package org.example.modelo;

import java.io.Serializable;

public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String cedula;
    private String cargo;
    private String sucursal;

    public Empleado(String nombre, String cedula, String cargo, String sucursal) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.cargo = cargo;
        this.sucursal = sucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public String getCargo() {
        return cargo;
    }

    public String getSucursal() {
        return sucursal;
    }
}