package org.example.modelo;

public class Pension {

    private double salario;
    private double valorPension;

    public Pension(double salario, double valorPension) {
        this.salario = salario;
        this.valorPension = valorPension;
    }

    public double getSalario() {
        return salario;
    }

    public double getValorPension() {
        return valorPension;
    }
}