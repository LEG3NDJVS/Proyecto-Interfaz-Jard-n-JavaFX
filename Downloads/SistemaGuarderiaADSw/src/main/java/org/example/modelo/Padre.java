package org.example.modelo;

public class Padre {

    private String nombre;
    private double salario;

    public Padre(String nombre, double salario) {
        this.nombre = nombre;
        this.salario = salario;
    }

    public String getNombre() {
        return nombre;
    }

    public double getSalario() {
        return salario;
    }
}