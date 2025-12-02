package com.marketlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marketlink.R;
import com.marketlink.models.DashboardMetric;

import java.util.List;

public class MetricAdapter extends RecyclerView.Adapter<MetricAdapter.MetricViewHolder> {

    private List<DashboardMetric> metrics;
    private OnMetricClickListener listener;

    public interface OnMetricClickListener {
        void onMetricClick(DashboardMetric metric);
    }

    public MetricAdapter(List<DashboardMetric> metrics, OnMetricClickListener listener) {
        this.metrics = metrics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MetricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_metric_card, parent, false);
        return new MetricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MetricViewHolder holder, int position) {
        DashboardMetric metric = metrics.get(position);
        holder.bind(metric, listener);
    }

    @Override
    public int getItemCount() {
        return metrics.size();
    }

    static class MetricViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout metricContainer;
        private ImageView ivMetricIcon;
        private TextView tvMetricValue;
        private TextView tvMetricTitle;

        public MetricViewHolder(@NonNull View itemView) {
            super(itemView);
            metricContainer = itemView.findViewById(R.id.metric_container);
            ivMetricIcon = itemView.findViewById(R.id.iv_metric_icon);
            tvMetricValue = itemView.findViewById(R.id.tv_metric_value);
            tvMetricTitle = itemView.findViewById(R.id.tv_metric_title);
        }

        public void bind(DashboardMetric metric, OnMetricClickListener listener) {
            tvMetricTitle.setText(metric.getTitle());
            tvMetricValue.setText(metric.getValue());
            ivMetricIcon.setImageResource(metric.getIconResId());
            metricContainer.setBackgroundResource(metric.getBackgroundDrawable());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMetricClick(metric);
                }
            });
        }
    }
}
