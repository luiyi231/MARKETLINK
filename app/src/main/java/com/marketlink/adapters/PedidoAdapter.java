package com.marketlink.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.marketlink.R;
import com.marketlink.models.Pedido;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidos;
    private OnPedidoActionListener listener;

    public interface OnPedidoActionListener {
        void onPedidoClick(Pedido pedido);

        void onCallClick(Pedido pedido);

        void onMapClick(Pedido pedido);
    }

    public PedidoAdapter(List<Pedido> pedidos, OnPedidoActionListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    public void updateList(List<Pedido> newPedidos) {
        this.pedidos = newPedidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_card, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido, listener);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardPedido;
        private TextView tvPedidoId;
        private TextView tvClientName;
        private TextView tvDate;
        private TextView tvTotal;
        private Chip chipStatus;
        private LinearLayout layoutActions;
        private MaterialButton btnCall;
        private MaterialButton btnMap;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPedido = itemView.findViewById(R.id.card_pedido);
            tvPedidoId = itemView.findViewById(R.id.tv_pedido_id);
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotal = itemView.findViewById(R.id.tv_total);
            chipStatus = itemView.findViewById(R.id.chip_status);
            layoutActions = itemView.findViewById(R.id.layout_actions);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnMap = itemView.findViewById(R.id.btn_map);
        }

        public void bind(Pedido pedido, OnPedidoActionListener listener) {
            tvPedidoId.setText("Pedido #" + pedido.getId());
            // Backend only provides ClienteId, not Name in this DTO. Using ID for now.
            tvClientName.setText("Cliente ID: " + pedido.getClienteId());
            tvDate.setText(
                    pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : "Fecha desconocida");
            tvTotal.setText(String.format("Bs. %.2f", pedido.getTotal()));
            chipStatus.setText(pedido.getEstado());

            // Set stroke color and width on MaterialCardView based on status
            int strokeColor;
            int strokeWidth = 6; // 6dp stroke width
            int chipBgColor;
            int chipTextColor = ContextCompat.getColor(itemView.getContext(), R.color.white);

            String estado = pedido.getEstado();
            if (estado == null)
                estado = "Pendiente";

            if (estado.equals("Pendiente")) {
                strokeColor = ContextCompat.getColor(itemView.getContext(), R.color.colorWarning);
                chipBgColor = R.color.colorWarning;
            } else if (estado.equals("Entregado")) {
                strokeColor = ContextCompat.getColor(itemView.getContext(), R.color.colorSuccess);
                chipBgColor = R.color.colorSuccess;
            } else if (estado.equals("En Curso") || estado.equals("En Ruta")) {
                strokeColor = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
                chipBgColor = R.color.colorPrimary;
            } else {
                // Cancelado
                strokeColor = ContextCompat.getColor(itemView.getContext(), R.color.colorOnSurface);
                chipBgColor = R.color.colorOnSurface;
            }

            // Apply stroke to MaterialCardView
            cardPedido.setStrokeWidth(strokeWidth);
            cardPedido.setStrokeColor(strokeColor);

            // Set chip colors
            chipStatus.setChipBackgroundColorResource(chipBgColor);
            chipStatus.setTextColor(chipTextColor);

            // Show/hide contextual actions based on status
            if (estado.equals("Pendiente") || estado.equals("En Curso")
                    || estado.equals("En Ruta")) {
                layoutActions.setVisibility(View.VISIBLE);
            } else {
                layoutActions.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPedidoClick(pedido);
                }
            });

            btnCall.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCallClick(pedido);
                }
            });

            btnMap.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMapClick(pedido);
                }
            });
        }
    }
}
