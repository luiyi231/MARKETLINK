package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.marketlink.R;
import com.marketlink.models.Categoria;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> categorias;
    private OnCategoriaClickListener listener;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    public CategoriaAdapter(List<Categoria> categorias, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria_card, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.bind(categoria, listener);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public void updateList(List<Categoria> newCategorias) {
        this.categorias = newCategorias;
        notifyDataSetChanged();
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardCategoria;
        private TextView tvNombre;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategoria = itemView.findViewById(R.id.card_categoria);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
        }

        public void bind(Categoria categoria, OnCategoriaClickListener listener) {
            tvNombre.setText(categoria.getNombre() != null ? categoria.getNombre() : "Sin nombre");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoriaClick(categoria);
                }
            });
        }
    }
}

