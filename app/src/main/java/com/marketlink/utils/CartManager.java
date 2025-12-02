package com.marketlink.utils;

import com.marketlink.models.CartItem;
import com.marketlink.models.Producto;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(Producto producto, int cantidad) {
        // Verificar si el producto ya está en el carrito
        for (CartItem item : cartItems) {
            if (item.getProducto().getId() != null && 
                item.getProducto().getId().equals(producto.getId())) {
                // Actualizar cantidad
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        // Agregar nuevo item
        cartItems.add(new CartItem(producto, cantidad));
    }

    public void removeItem(String productoId) {
        cartItems.removeIf(item -> 
            item.getProducto().getId() != null && 
            item.getProducto().getId().equals(productoId));
    }

    public void updateQuantity(String productoId, int cantidad) {
        for (CartItem item : cartItems) {
            if (item.getProducto().getId() != null && 
                item.getProducto().getId().equals(productoId)) {
                if (cantidad <= 0) {
                    removeItem(productoId);
                } else {
                    item.setCantidad(cantidad);
                }
                return;
            }
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getCantidad();
        }
        return count;
    }

    public void clear() {
        cartItems.clear();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}

