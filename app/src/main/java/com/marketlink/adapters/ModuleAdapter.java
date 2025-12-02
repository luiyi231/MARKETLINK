package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marketlink.R;
import com.marketlink.models.Module;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> modules;
    private OnModuleClickListener listener;

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }

    public ModuleAdapter(List<Module> modules, OnModuleClickListener listener) {
        this.modules = modules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module_card, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = modules.get(position);
        holder.bind(module, listener);
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout cardBackground;
        private ImageView ivModuleIcon;
        private TextView tvModuleName;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBackground = itemView.findViewById(R.id.card_background);
            ivModuleIcon = itemView.findViewById(R.id.iv_module_icon);
            tvModuleName = itemView.findViewById(R.id.tv_module_name);
        }

        public void bind(Module module, OnModuleClickListener listener) {
            tvModuleName.setText(module.getName());
            ivModuleIcon.setImageResource(module.getIconResId());
            cardBackground.setBackgroundResource(module.getGradientResId());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onModuleClick(module);
                }
            });
        }
    }
}