package com.marketlink.models;

public class ProductoConPerfil {
    private Producto producto;
    private PerfilComercial perfilComercial;

    public ProductoConPerfil(Producto producto, PerfilComercial perfilComercial) {
        this.producto = producto;
        this.perfilComercial = perfilComercial;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public PerfilComercial getPerfilComercial() {
        return perfilComercial;
    }

    public void setPerfilComercial(PerfilComercial perfilComercial) {
        this.perfilComercial = perfilComercial;
    }

    public String getNombreEmpresa() {
        if (perfilComercial != null && perfilComercial.getNombre() != null) {
            return perfilComercial.getNombre();
        }
        return "Empresa";
    }
}

