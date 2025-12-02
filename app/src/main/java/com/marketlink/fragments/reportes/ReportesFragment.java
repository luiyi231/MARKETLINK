package com.marketlink.fragments.reportes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.marketlink.MainActivity;
import com.marketlink.R;

public class ReportesFragment extends Fragment {

    private ChipGroup chipGroupTime;
    private Chip chipHoy;
    private Chip chip7Dias;
    private Chip chipMes;
    private MaterialButton btnExport;
    private MaterialButton btnDeliveryReport;

    public ReportesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reportes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar with Navigation Drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            
            DrawerLayout drawerLayout = 
                ((MainActivity) getActivity()).getDrawerLayout();
            
            if (drawerLayout != null) {
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.dashboardFragment, R.id.pedidosListFragment, R.id.productCatalogFragment, 
                        R.id.empresaListFragment, R.id.userListFragment, R.id.reportesFragment
                ).setOpenableLayout(drawerLayout).build();
                
                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        // Initialize views
        chipGroupTime = view.findViewById(R.id.chip_group_time);
        chipHoy = view.findViewById(R.id.chip_hoy);
        chip7Dias = view.findViewById(R.id.chip_7_dias);
        chipMes = view.findViewById(R.id.chip_mes);
        btnExport = view.findViewById(R.id.btn_export);
        btnDeliveryReport = view.findViewById(R.id.btn_delivery_report);

        // Setup time filter chips
        chipHoy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Filtro: Hoy", Toast.LENGTH_SHORT).show();
                // TODO: Apply filter
            }
        });

        chip7Dias.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Filtro: Últimos 7 Días", Toast.LENGTH_SHORT).show();
                // TODO: Apply filter
            }
        });

        chipMes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Filtro: Este Mes", Toast.LENGTH_SHORT).show();
                // TODO: Apply filter
            }
        });

        // Export button
        btnExport.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Exportando histórico...", Toast.LENGTH_SHORT).show();
            // TODO: Implement export logic
        });

        // Delivery report button
        btnDeliveryReport.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Generando reporte de repartidores...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to delivery report
        });
    }
}

