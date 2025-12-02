package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.marketlink.R;
import com.marketlink.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(List<User> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardUser;
        private View avatarCircle;
        private ImageView ivUserIcon;
        private TextView tvUserName;
        private TextView tvUserEmail;
        private Chip chipRol;
        private Chip chipEstado;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardUser = itemView.findViewById(R.id.card_user);
            avatarCircle = itemView.findViewById(R.id.avatar_circle);
            ivUserIcon = itemView.findViewById(R.id.iv_user_icon);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            chipRol = itemView.findViewById(R.id.chip_rol);
            chipEstado = itemView.findViewById(R.id.chip_estado);
        }

        public void bind(User user, OnUserClickListener listener) {
            tvUserName.setText(user.getNombreCompleto());
            tvUserEmail.setText(user.getEmail());
            chipRol.setText(user.getRol());
            chipEstado.setText(user.getEstadoCuenta());

            // Set avatar circle color based on role (Diferenciación Visual Inmediata)
            int avatarBgResId;
            int roleChipColor;
            String rol = user.getRol().toUpperCase();
            
            if (rol.equals("ADMINISTRADOR") || rol.equals("ADMIN")) {
                // Administrador: Avatar Azul Rey (#3B82F6), Chip Morado (#9333EA)
                avatarBgResId = R.drawable.bg_avatar_admin;
                roleChipColor = R.color.colorSecondary; // Morado - Rol de Permiso
            } else if (rol.equals("REPARTIDOR") || rol.equals("DELIVERY")) {
                // Repartidor: Avatar Naranja Fuerte (#F97316), Chip Turquesa (#14B8A6)
                avatarBgResId = R.drawable.bg_avatar_repartidor;
                roleChipColor = R.color.colorAccent; // Turquesa - Rol Operativo
            } else {
                // CLIENTE or default: Avatar Verde Vigoroso (#22C55E), Chip Verde (#22C55E)
                avatarBgResId = R.drawable.bg_avatar_cliente;
                roleChipColor = R.color.colorSuccess; // Verde - Rol de Cliente
            }
            
            avatarCircle.setBackgroundResource(avatarBgResId);

            // Set role chip color (Morado para Admin, Turquesa para Repartidor)
            chipRol.setChipBackgroundColorResource(roleChipColor);
            chipRol.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

            // Set status chip color (Verde Vigoroso para Activo, Gris para Inactivo)
            int estadoBgColor;
            String estado = user.getEstadoCuenta().toUpperCase();
            
            if (estado.equals("ACTIVO") || estado.equals("ACTIVE")) {
                // Activo: Verde Vigoroso (#22C55E) con borde sutil verde oscuro
                estadoBgColor = R.color.colorSuccess;
                chipEstado.setChipStrokeColorResource(R.color.green_dark_border);
                chipEstado.setChipStrokeWidth(1);
            } else {
                // INACTIVO or default: Gris Oscuro (#6B7280) - colorTextSecondary
                estadoBgColor = R.color.colorTextSecondary;
                chipEstado.setChipStrokeWidth(0); // Sin borde para inactivo
            }
            
            chipEstado.setChipBackgroundColorResource(estadoBgColor);
            chipEstado.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

            // Card click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }
    }
}

