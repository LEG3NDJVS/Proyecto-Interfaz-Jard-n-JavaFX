package org.example.modelo;

import java.io.Serializable;

public class CarnetVacunas implements Serializable {

    private static final long serialVersionUID = 1L;

    private String observacion;

    public CarnetVacunas(String observacion) {
        this.observacion = observacion;
    }

    public String getObservacion() {
        return observacion;
    }
}