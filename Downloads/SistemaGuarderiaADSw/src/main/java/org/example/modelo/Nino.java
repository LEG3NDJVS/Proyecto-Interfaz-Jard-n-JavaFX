package org.example.modelo;

import java.io.Serializable;

public class Nino implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreCompleto;
    private String fechaNacimiento;
    private String registroCivil;
    private String marcaPanial;
    private String marcaLeche;
    private String tipoInscripcion;

    private Dieta dieta;
    private CarnetVacunas carnetVacunas;

    public Nino(String nombreCompleto,
                String fechaNacimiento,
                String registroCivil,
                String marcaPanial,
                String marcaLeche,
                String tipoInscripcion) {

        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.registroCivil = registroCivil;
        this.marcaPanial = marcaPanial;
        this.marcaLeche = marcaLeche;
        this.tipoInscripcion = tipoInscripcion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getRegistroCivil() {
        return registroCivil;
    }

    public String getMarcaPanial() {
        return marcaPanial;
    }

    public String getMarcaLeche() {
        return marcaLeche;
    }

    public String getTipoInscripcion() {
        return tipoInscripcion;
    }

    public Dieta getDieta() {
        return dieta;
    }

    public void setDieta(Dieta dieta) {
        this.dieta = dieta;
    }

    public CarnetVacunas getCarnetVacunas() {
        return carnetVacunas;
    }

    public void setCarnetVacunas(CarnetVacunas carnetVacunas) {
        this.carnetVacunas = carnetVacunas;
    }
}