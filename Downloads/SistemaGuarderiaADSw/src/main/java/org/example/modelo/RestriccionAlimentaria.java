package org.example.modelo;

public class RestriccionAlimentaria {

    private String alimento;
    private String motivo;

    public RestriccionAlimentaria(String alimento, String motivo) {
        this.alimento = alimento;
        this.motivo = motivo;
    }

    public String getAlimento() {
        return alimento;
    }

    public String getMotivo() {
        return motivo;
    }
}