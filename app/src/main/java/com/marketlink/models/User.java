package com.marketlink.models;

public class User {
    private String id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String rol; // "ADMINISTRADOR", "REPARTIDOR", "CLIENTE"
    private String estadoCuenta; // "ACTIVO", "INACTIVO"
    private String tipoUsuario; // "Empresa", "Cliente", "Administrador"

    public User(String id, String nombreCompleto, String email, String telefono, 
                String rol, String estadoCuenta, String tipoUsuario) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
        this.estadoCuenta = estadoCuenta;
        this.tipoUsuario = tipoUsuario;
    }

    public String getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRol() {
        return rol;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}

