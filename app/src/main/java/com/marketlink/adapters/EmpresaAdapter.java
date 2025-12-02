package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.marketlink.R;
import com.marketlink.models.Empresa;

import java.util.List;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder> {

    private List<Empresa> empresas;
    private OnEmpresaActionListener listener;

    public interface OnEmpresaActionListener {
        void onEmpresaClick(Empresa empresa);
        void onEditClick(Empresa empresa);
        void onBlockClick(Empresa empresa);
        void onViewSucursalesClick(Empresa empresa);
    }

    public EmpresaAdapter(List<Empresa> empresas, OnEmpresaActionListener listener) {
        this.empresas = empresas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmpresaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empresa_card, parent, false);
        return new EmpresaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpresaViewHolder holder, int position) {
        Empresa empresa = empresas.get(position);
        holder.bind(empresa, listener);
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    static class EmpresaViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardEmpresa;
        private View statusIndicator;
        private TextView tvEmpresaNombre;
        private TextView tvEmpresaRuc;
        private TextView tvEmpresaContacto;
        private Chip chipPlan;
        private ImageButton btnDetail;
        private ImageButton btnMore;

        public EmpresaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEmpresa = itemView.findViewById(R.id.card_empresa);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            tvEmpresaNombre = itemView.findViewById(R.id.tv_empresa_nombre);
            tvEmpresaRuc = itemView.findViewById(R.id.tv_empresa_ruc);
            tvEmpresaContacto = itemView.findViewById(R.id.tv_empresa_contacto);
            chipPlan = itemView.findViewById(R.id.chip_plan);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            btnMore = itemView.findViewById(R.id.btn_more);
        }

        public void bind(Empresa empresa, OnEmpresaActionListener listener) {
            tvEmpresaNombre.setText(empresa.getRazonSocial() != null ? empresa.getRazonSocial() : "Sin nombre");
            tvEmpresaRuc.setText("NIT: " + (empresa.getNit() != null ? empresa.getNit() : "N/A"));
            
            String telefono = empresa.getTelefono() != null ? empresa.getTelefono() : "N/A";
            String email = empresa.getEmail() != null ? empresa.getEmail() : "N/A";
            tvEmpresaContacto.setText("Tel: " + telefono + " | " + email);
            
            // Plan info not available in Empresa model, show default
            chipPlan.setText("Plan Básico");

            // Set status indicator color based on activa field
            int statusColor;
            Boolean activa = empresa.getActiva();
            
            if (activa != null && activa) {
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorSuccess);
            } else {
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorTextSecondary);
            }

            statusIndicator.setBackgroundColor(statusColor);

            // Set chip plan color (always colorSecondary for visibility)
            chipPlan.setChipBackgroundColorResource(R.color.colorSecondary);
            chipPlan.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

            // Setup more options menu
            btnMore.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), btnMore);
                popupMenu.getMenuInflater().inflate(R.menu.empresa_context_menu, popupMenu.getMenu());
                
                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit) {
                        if (listener != null) {
                            listener.onEditClick(empresa);
                        }
                        return true;
                    } else if (itemId == R.id.menu_block) {
                        if (listener != null) {
                            listener.onBlockClick(empresa);
                        }
                        return true;
                    } else if (itemId == R.id.menu_view_sucursales) {
                        if (listener != null) {
                            listener.onViewSucursalesClick(empresa);
                        }
                        return true;
                    }
                    return false;
                });
                
                popupMenu.show();
            });

            // Detail button click listener
            btnDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEmpresaClick(empresa);
                }
            });

            // Card click listener (also navigates to detail)
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEmpresaClick(empresa);
                }
            });
        }
    }
}

