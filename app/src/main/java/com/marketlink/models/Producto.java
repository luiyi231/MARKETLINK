package com.marketlink.models;

import java.util.Date;

public class Producto {
    private String id;
    private String perfilComercialId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoriaId;
    private String subcategoriaId;
    private String imagenUrl;
    private Integer stock;
    private Boolean disponible;
    private Date fechaCreacion;
    private Date fechaUltimaActualizacion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerfilComercialId() {
        return perfilComercialId;
    }

    public void setPerfilComercialId(String perfilComercialId) {
        this.perfilComercialId = perfilComercialId;
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

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getSubcategoriaId() {
        return subcategoriaId;
    }

    public void setSubcategoriaId(String subcategoriaId) {
        this.subcategoriaId = subcategoriaId;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(Date fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
}
