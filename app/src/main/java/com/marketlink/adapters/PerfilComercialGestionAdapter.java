package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.marketlink.R;
import com.marketlink.models.PerfilComercial;

import java.util.List;

public class PerfilComercialGestionAdapter extends RecyclerView.Adapter<PerfilComercialGestionAdapter.PerfilViewHolder> {

    private List<PerfilComercial> perfiles;
    private OnPerfilActionListener listener;

    public interface OnPerfilActionListener {
        void onEditarPerfil(PerfilComercial perfil);
        void onVerProductos(PerfilComercial perfil);
    }

    public PerfilComercialGestionAdapter(List<PerfilComercial> perfiles, OnPerfilActionListener listener) {
        this.perfiles = perfiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_perfil_gestion, parent, false);
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
        private MaterialCardView cardPerfil;
        private View statusIndicator;
        private TextView tvNombre;
        private TextView tvDireccion;
        private TextView tvContacto;
            private MaterialButton btnEditar;
            private MaterialButton btnVerProductos;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPerfil = itemView.findViewById(R.id.card_perfil);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDireccion = itemView.findViewById(R.id.tv_direccion);
            tvContacto = itemView.findViewById(R.id.tv_contacto);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnVerProductos = itemView.findViewById(R.id.btn_ver_productos);
        }

        public void bind(PerfilComercial perfil, OnPerfilActionListener listener) {
            tvNombre.setText(perfil.getNombre() != null ? perfil.getNombre() : "Sin nombre");
            tvDireccion.setText(perfil.getDireccion() != null ? perfil.getDireccion() : "Sin dirección");
            
            String telefono = perfil.getTelefono() != null ? perfil.getTelefono() : "N/A";
            String email = perfil.getEmail() != null ? perfil.getEmail() : "N/A";
            tvContacto.setText("📞 " + telefono + " | ✉️ " + email);

            // Set status indicator
            int statusColor;
            Boolean activo = perfil.getActivo();
            if (activo != null && activo) {
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorSuccess);
            } else {
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorTextSecondary);
            }
            statusIndicator.setBackgroundColor(statusColor);

            // Botones de acción
            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditarPerfil(perfil);
                }
            });

            btnVerProductos.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerProductos(perfil);
                }
            });
        }
    }
}

