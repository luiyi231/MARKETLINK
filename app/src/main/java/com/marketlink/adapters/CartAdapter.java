package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marketlink.R;
import com.marketlink.models.CartItem;
import com.marketlink.utils.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onQuantityChanged();
        void onItemRemoved();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateList(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvQuantity;
        private com.google.android.material.button.MaterialButton btnDecrease;
        private com.google.android.material.button.MaterialButton btnIncrease;
        private com.google.android.material.button.MaterialButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(CartItem cartItem, OnCartItemListener listener) {
            com.marketlink.models.Producto producto = cartItem.getProducto();
            
            tvProductName.setText(producto.getNombre() != null ? producto.getNombre() : "Sin nombre");
            
            // Format price
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "BO"));
            if (producto.getPrecio() != null) {
                tvProductPrice.setText(formatter.format(producto.getPrecio()));
            } else {
                tvProductPrice.setText("Bs. 0.00");
            }
            
            tvQuantity.setText(String.valueOf(cartItem.getCantidad()));

            // Handle image
            if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(producto.getImagenUrl())
                        .placeholder(R.drawable.ic_package_2)
                        .error(R.drawable.ic_package_2)
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_package_2);
            }

            // Quantity controls
            btnDecrease.setOnClickListener(v -> {
                if (cartItem.getCantidad() > 1) {
                    CartManager.getInstance().updateQuantity(
                        producto.getId(), cartItem.getCantidad() - 1);
                    if (listener != null) {
                        listener.onQuantityChanged();
                    }
                }
            });

            btnIncrease.setOnClickListener(v -> {
                CartManager.getInstance().updateQuantity(
                    producto.getId(), cartItem.getCantidad() + 1);
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            });

            btnRemove.setOnClickListener(v -> {
                CartManager.getInstance().removeItem(producto.getId());
                if (listener != null) {
                    listener.onItemRemoved();
                }
            });
        }
    }
}

