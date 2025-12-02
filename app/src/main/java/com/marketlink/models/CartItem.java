package com.marketlink.models;

public class CartItem {
    private Producto producto;
    private int cantidad;

    public CartItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        if (producto != null && producto.getPrecio() != null) {
            return producto.getPrecio() * cantidad;
        }
        return 0.0;
    }
}

