package com.marketlink.fragments.tracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.marketlink.R;

public class TrackingFragment extends Fragment {

    private TextView tvOrderId;
    private TextView tvClientInfo;
    private TextView tvEta;
    private TextView tvStatus;
    private LinearProgressIndicator progressDelivery;
    private MaterialButton btnReassign;
    private MaterialButton btnMarkDelivered;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    
    // Datos del pedido recibidos por argumentos
    private String pedidoId;
    private String pedidoCliente;
    private String pedidoStatus;

    public TrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Leer argumentos del pedido
        if (getArguments() != null) {
            pedidoId = getArguments().getString("pedido_id", "N/A");
            pedidoCliente = getArguments().getString("pedido_cliente", "Cliente Desconocido");
            pedidoStatus = getArguments().getString("pedido_status", "Pendiente");
        }

        // Initialize views
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        tvOrderId = view.findViewById(R.id.tv_order_id);
        tvClientInfo = view.findViewById(R.id.tv_client_info);
        tvEta = view.findViewById(R.id.tv_eta);
        tvStatus = view.findViewById(R.id.tv_status);
        progressDelivery = view.findViewById(R.id.progress_delivery);
        btnReassign = view.findViewById(R.id.btn_reassign);
        btnMarkDelivered = view.findViewById(R.id.btn_mark_delivered);

        // Setup BottomSheet behavior
        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(400);
            bottomSheetBehavior.setHideable(false);
        }

        // Cargar datos del pedido
        loadTrackingData();

        // Setup action buttons
        btnReassign.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Reasignar repartidor", Toast.LENGTH_SHORT).show();
            // TODO: Show dialog to select new delivery person
        });

        btnMarkDelivered.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pedido marcado como entregado ✓", Toast.LENGTH_SHORT).show();
            pedidoStatus = "Entregado";
            tvStatus.setText("✓ Entregado");
            progressDelivery.setProgress(100);
        });
    }

    private void loadTrackingData() {
        // Mostrar información del pedido
        tvOrderId.setText("Pedido #" + pedidoId);
        tvClientInfo.setText("Cliente: " + pedidoCliente);
        
        // Configurar progreso y ETA según el estado
        int progress;
        String eta;
        String statusText;
        
        switch (pedidoStatus) {
            case "Pendiente":
                progress = 20;
                eta = "En preparación";
                statusText = "⏳ Pendiente";
                break;
            case "En Curso":
            case "En Ruta":
                progress = 60;
                eta = "ETA: 15 minutos";
                statusText = "🚚 En Ruta";
                break;
            case "Entregado":
                progress = 100;
                eta = "Completado";
                statusText = "✓ Entregado";
                break;
            default:
                progress = 0;
                eta = "Desconocido";
                statusText = "❓ " + pedidoStatus;
        }
        
        progressDelivery.setProgress(progress);
        tvEta.setText(eta);
        tvStatus.setText(statusText);
    }
}
