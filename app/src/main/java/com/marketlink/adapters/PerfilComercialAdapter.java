package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.marketlink.R;
import com.marketlink.models.PerfilComercial;

import java.util.List;

public class PerfilComercialAdapter extends RecyclerView.Adapter<PerfilComercialAdapter.PerfilViewHolder> {

    private List<PerfilComercial> perfiles;
    private OnPerfilClickListener listener;

    public interface OnPerfilClickListener {
        void onPerfilClick(PerfilComercial perfil);
    }

    public PerfilComercialAdapter(List<PerfilComercial> perfiles, OnPerfilClickListener listener) {
        this.perfiles = perfiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empresa_card, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        PerfilComercial perfil = perfiles.get(position);
        holder.bind(perfil, listener);
    }

    @Override
    public int getItemCount() {
        return perfiles.size();
    }

    public void updateList(List<PerfilComercial> newPerfiles) {
        this.perfiles = newPerfiles;
        notifyDataSetChanged();
    }

    static class PerfilViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardEmpresa;
        private View statusIndicator;
        private TextView tvEmpresaNombre;
        private TextView tvEmpresaRuc;
        private TextView tvEmpresaContacto;
        private Chip chipPlan;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEmpresa = itemView.findViewById(R.id.card_empresa);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            tvEmpresaNombre = itemView.findViewById(R.id.tv_empresa_nombre);
            tvEmpresaRuc = itemView.findViewById(R.id.tv_empresa_ruc);
            tvEmpresaContacto = itemView.findViewById(R.id.tv_empresa_contacto);
            chipPlan = itemView.findViewById(R.id.chip_plan);
        }

        public void bind(PerfilComercial perfil, OnPerfilClickListener listener) {
            tvEmpresaNombre.setText(perfil.getNombre() != null ? perfil.getNombre() : "Sin nombre");
            tvEmpresaRuc.setText(perfil.getDireccion() != null ? perfil.getDireccion() : "Sin dirección");
            
            String telefono = perfil.getTelefono() != null ? perfil.getTelefono() : "N/A";
            String email = perfil.getEmail() != null ? perfil.getEmail() : "N/A";
            tvEmpresaContacto.setText("Tel: " + telefono + " | " + email);
            
            chipPlan.setText("Ver productos");
            chipPlan.setChipBackgroundColorResource(R.color.colorSecondary);
            chipPlan.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

            // Set status indicator
            int statusColor;
            Boolean activo = perfil.getActivo();
            if (activo != null && activo) {
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorSuccess);
            } else {
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorTextSecondary);
            }
            statusIndicator.setBackgroundColor(statusColor);

            // Hide more button for clients
            View btnMore = itemView.findViewById(R.id.btn_more);
            if (btnMore != null) {
                btnMore.setVisibility(View.GONE);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPerfilClick(perfil);
                }
            });

            // Detail button
            View btnDetail = itemView.findViewById(R.id.btn_detail);
            if (btnDetail != null) {
                btnDetail.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPerfilClick(perfil);
                    }
                });
            }
        }
    }
}

