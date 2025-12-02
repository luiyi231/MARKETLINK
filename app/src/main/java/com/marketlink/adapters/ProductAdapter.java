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
import com.marketlink.models.Producto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Producto> products;
    private java.util.Map<String, String> perfilesMap; // Map de perfilId -> nombre
    private OnProductClickListener listener;
    private boolean showAddToCartButton; // Controlar visibilidad del botón

    public interface OnProductClickListener {
        void onProductClick(Producto producto);
        void onAddToCartClick(Producto producto);
    }

    public ProductAdapter(List<Producto> products, OnProductClickListener listener) {
        this.products = products != null ? products : new ArrayList<>();
        this.perfilesMap = new java.util.HashMap<>();
        this.listener = listener;
        this.showAddToCartButton = true; // Por defecto visible
    }

    public void setShowAddToCartButton(boolean show) {
        this.showAddToCartButton = show;
        notifyDataSetChanged();
    }

    public void updateList(List<Producto> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setPerfilesMap(java.util.Map<String, String> perfilesMap) {
        this.perfilesMap = perfilesMap != null ? perfilesMap : new java.util.HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Producto producto = products.get(position);
        String empresaNombre = null;
        if (producto.getPerfilComercialId() != null && perfilesMap != null) {
            empresaNombre = perfilesMap.get(producto.getPerfilComercialId());
        }
        holder.bind(producto, empresaNombre, listener, showAddToCartButton);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private ImageView ivPlaceholderIcon;
        private TextView tvProductName;
        private TextView tvEmpresaName;
        private TextView tvProductPrice;
        private com.google.android.material.button.MaterialButton btnAddCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            ivPlaceholderIcon = itemView.findViewById(R.id.iv_placeholder_icon);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvEmpresaName = itemView.findViewById(R.id.tv_empresa_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            btnAddCart = itemView.findViewById(R.id.btn_add_cart);
        }

        public void bind(Producto producto, String empresaNombre, OnProductClickListener listener, boolean showAddToCart) {
            tvProductName.setText(producto.getNombre() != null ? producto.getNombre() : "Sin nombre");
            
            // Mostrar nombre de empresa/perfil comercial
            if (empresaNombre != null && !empresaNombre.isEmpty()) {
                tvEmpresaName.setVisibility(View.VISIBLE);
                tvEmpresaName.setText(empresaNombre);
            } else {
                tvEmpresaName.setVisibility(View.GONE);
            }
            
            // Format price
            if (producto.getPrecio() != null) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "BO"));
                tvProductPrice.setText(formatter.format(producto.getPrecio()));
            } else {
                tvProductPrice.setText("Bs. 0.00");
            }

            // Handle image
            if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                ivProductImage.setVisibility(View.VISIBLE);
                ivPlaceholderIcon.setVisibility(View.GONE);
                Glide.with(itemView.getContext())
                        .load(producto.getImagenUrl())
                        .placeholder(R.drawable.ic_package_2)
                        .error(R.drawable.ic_package_2)
                        .into(ivProductImage);
            } else {
                // Use placeholder
                ivProductImage.setVisibility(View.GONE);
                ivPlaceholderIcon.setVisibility(View.VISIBLE);
                ivPlaceholderIcon.setImageResource(R.drawable.ic_package_2);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(producto);
                }
            });

            // Mostrar/ocultar botón según configuración
            if (showAddToCart) {
                btnAddCart.setVisibility(View.VISIBLE);
                btnAddCart.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAddToCartClick(producto);
                    }
                });
            } else {
                btnAddCart.setVisibility(View.GONE);
            }
        }
    }
}
