package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.marketlink.R;
import com.marketlink.models.Plan;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<Plan> planes;
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(Plan plan);
    }

    public PlanAdapter(List<Plan> planes, OnPlanClickListener listener) {
        this.planes = planes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_card, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planes.get(position);
        holder.bind(plan, listener);
    }

    @Override
    public int getItemCount() {
        return planes.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardPlan;
        private TextView tvNombre;
        private TextView tvDescripcion;
        private TextView tvPrecio;
        private TextView tvMaxPerfiles;
        private TextView tvMaxProductos;
        private TextView tvDuracion;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPlan = itemView.findViewById(R.id.card_plan);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            tvMaxPerfiles = itemView.findViewById(R.id.tv_max_perfiles);
            tvMaxProductos = itemView.findViewById(R.id.tv_max_productos);
            tvDuracion = itemView.findViewById(R.id.tv_duracion);
        }

        public void bind(Plan plan, OnPlanClickListener listener) {
            tvNombre.setText(plan.getNombre() != null ? plan.getNombre() : "Sin nombre");
            tvDescripcion.setText(plan.getDescripcion() != null ? plan.getDescripcion() : "");
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            String precio = plan.getPrecio() != null ? currencyFormat.format(plan.getPrecio()) : "$0.00";
            tvPrecio.setText(precio);
            
            tvMaxPerfiles.setText("Máx. Perfiles: " + (plan.getMaxPerfilesComerciales() != null ? plan.getMaxPerfilesComerciales() : 0));
            tvMaxProductos.setText("Máx. Productos: " + (plan.getMaxProductos() != null ? plan.getMaxProductos() : 0));
            
            String duracion = plan.getDuracionDias() != null ? plan.getDuracionDias() + " días" : "N/A";
            tvDuracion.setText("Duración: " + duracion);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlanClick(plan);
                }
            });
        }
    }
}

