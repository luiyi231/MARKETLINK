package com.marketlink.models;

public class UpdateEstadoRequest {
    private String estado;

    public UpdateEstadoRequest(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
