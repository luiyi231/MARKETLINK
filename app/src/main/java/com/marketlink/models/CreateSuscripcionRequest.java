package com.marketlink.models;

public class CreateSuscripcionRequest {
    private String planId;
    private String empresaId; // Opcional, solo para admin

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }
}

