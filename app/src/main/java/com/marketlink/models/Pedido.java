package com.marketlink.models;

import java.util.Date;
import java.util.List;

public class Pedido {
    private String id;
    private String clienteId;
    private String perfilComercialId;
    private List<PedidoItem> items;
    private Double subtotal;
    private Double total;
    private String estado;
    private String direccionEntrega;
    private Double latitudEntrega;
    private Double longitudEntrega;
    private String notas;
    private Date fechaCreacion;
    private Date fechaActualizacion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getPerfilComercialId() {
        return perfilComercialId;
    }

    public void setPerfilComercialId(String perfilComercialId) {
        this.perfilComercialId = perfilComercialId;
    }

    public List<PedidoItem> getItems() {
        return items;
    }

    public void setItems(List<PedidoItem> items) {
        this.items = items;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public Double getLatitudEntrega() {
        return latitudEntrega;
    }

    public void setLatitudEntrega(Double latitudEntrega) {
        this.latitudEntrega = latitudEntrega;
    }

    public Double getLongitudEntrega() {
        return longitudEntrega;
    }

    public void setLongitudEntrega(Double longitudEntrega) {
        this.longitudEntrega = longitudEntrega;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
