package com.marketlink.models;

import java.util.Date;

public class Plan {
    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer duracionDias;
    private Integer maxPerfilesComerciales;
    private Integer maxProductos;
    private Boolean activo;
    private Date fechaCreacion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public Integer getMaxPerfilesComerciales() {
        return maxPerfilesComerciales;
    }

    public void setMaxPerfilesComerciales(Integer maxPerfilesComerciales) {
        this.maxPerfilesComerciales = maxPerfilesComerciales;
    }

    public Integer getMaxProductos() {
        return maxProductos;
    }

    public void setMaxProductos(Integer maxProductos) {
        this.maxProductos = maxProductos;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

