package com.marketlink.fragments.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.marketlink.MainActivity;
import com.marketlink.R;
import com.marketlink.adapters.MetricAdapter;
import com.marketlink.adapters.ModuleAdapter;
import com.marketlink.models.DashboardMetric;
import com.marketlink.models.Module;
import com.marketlink.models.Pedido;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView rvMetrics;
    private RecyclerView rvModules;
    private MetricAdapter metricAdapter;
    private ModuleAdapter moduleAdapter;
    private ProgressBar progressBar;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Verificar tipo de usuario - Solo empresas y admin pueden ver dashboard
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs",
                android.content.Context.MODE_PRIVATE);
        String tipoUsuario = prefs.getString("user_tipo", "Cliente");

        if ("Cliente".equals(tipoUsuario)) {
            // Redirigir clientes a HomeFragment
            Navigation.findNavController(view).navigate(R.id.action_dashboardFragment_to_homeFragment);
            return;
        }

        // Setup Toolbar with Navigation Drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            // Get drawer layout from MainActivity
            androidx.drawerlayout.widget.DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();

            if (drawerLayout != null) {
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.dashboardFragment, R.id.pedidosListFragment, R.id.productCatalogFragment)
                        .setOpenableLayout(drawerLayout).build();

                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        rvMetrics = view.findViewById(R.id.rv_metrics);
        rvModules = view.findViewById(R.id.rv_modules);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup Metrics Row
        rvMetrics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        // Cargar métricas desde el backend
        loadMetrics();
        
        metricAdapter = new MetricAdapter(new ArrayList<>(), metric -> {
            // Navigate to filtered list based on metric
            if (metric.getFilterType().equals("PENDIENTE")) {
                // Navigate to orders with filter
                Bundle args = new Bundle();
                args.putString("filter", "Pendiente");
                Navigation.findNavController(view).navigate(R.id.pedidosListFragment, args);
            } else {
                Toast.makeText(getContext(), metric.getTitle() + ": " + metric.getValue(), Toast.LENGTH_SHORT).show();
            }
        });
        rvMetrics.setAdapter(metricAdapter);

        // Setup Modules Grid
        rvModules.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<Module> modules = getModules();
        moduleAdapter = new ModuleAdapter(modules, module -> {
            // Navigate to respective module based on navigationId
            String navId = module.getNavigationId();
            if (navId != null && !navId.isEmpty()) {
                int destinationId = 0;
                // Use direct resource IDs instead of getIdentifier
                switch (navId) {
                    case "pedidosListFragment":
                        destinationId = R.id.pedidosListFragment;
                        break;
                    case "productCatalogFragment":
                        destinationId = R.id.productCatalogFragment;
                        break;
                    case "planesYPerfilesFragment":
                        destinationId = R.id.planesYPerfilesFragment;
                        break;
                    case "nav_companies":
                        // Navigate to EmpresaListFragment
                        destinationId = R.id.empresaListFragment;
                        break;
                    case "nav_reports":
                        // Navigate to ReportesFragment
                        destinationId = R.id.reportesFragment;
                        break;
                    case "nav_settings":
                        // Navigate to ConfiguracionFragment
                        destinationId = R.id.configuracionFragment;
                        break;
                    default:
                        Toast.makeText(getContext(), module.getName(), Toast.LENGTH_SHORT).show();
                        return;
                }

                if (destinationId != 0) {
                    Navigation.findNavController(view).navigate(destinationId);
                }
            } else {
                Toast.makeText(getContext(), module.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        rvModules.setAdapter(moduleAdapter);
    }

    private void loadMetrics() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        
        if (token == null || token.isEmpty()) {
            // Si no hay token, mostrar métricas vacías
            List<DashboardMetric> emptyMetrics = new ArrayList<>();
            emptyMetrics.add(new DashboardMetric("Pendientes", "0", android.R.drawable.ic_menu_recent_history,
                    R.drawable.bg_gradient_metric_pending, "PENDIENTE"));
            emptyMetrics.add(new DashboardMetric("Ingreso Hoy", "Bs. 0", android.R.drawable.ic_menu_sort_by_size,
                    R.drawable.bg_gradient_metric_income, "INGRESOS"));
            metricAdapter = new MetricAdapter(emptyMetrics, metric -> {
                if (metric.getFilterType().equals("PENDIENTE")) {
                    Bundle args = new Bundle();
                    args.putString("filter", "Pendiente");
                    Navigation.findNavController(requireView()).navigate(R.id.pedidosListFragment, args);
                }
            });
            rvMetrics.setAdapter(metricAdapter);
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Pedido>> call = apiService.getMisPedidos();
        
        call.enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                List<DashboardMetric> metrics = new ArrayList<>();
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Pedido> pedidos = response.body();
                    
                    // Calcular pedidos pendientes
                    long pendientes = pedidos.stream()
                        .filter(p -> p.getEstado() != null && 
                            (p.getEstado().equals("Pendiente") || p.getEstado().equals("PENDIENTE")))
                        .count();
                    
                    // Calcular ingreso de hoy
                    double ingresoHoy = 0.0;
                    java.util.Calendar hoy = java.util.Calendar.getInstance();
                    hoy.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    hoy.set(java.util.Calendar.MINUTE, 0);
                    hoy.set(java.util.Calendar.SECOND, 0);
                    hoy.set(java.util.Calendar.MILLISECOND, 0);
                    
                    for (Pedido pedido : pedidos) {
                        if (pedido.getFechaCreacion() != null && 
                            pedido.getFechaCreacion().after(hoy.getTime()) &&
                            pedido.getTotal() != null) {
                            ingresoHoy += pedido.getTotal();
                        }
                    }
                    
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "BO"));
                    
                    metrics.add(new DashboardMetric("Pendientes", String.valueOf(pendientes), 
                        android.R.drawable.ic_menu_recent_history,
                        R.drawable.bg_gradient_metric_pending, "PENDIENTE"));
                    metrics.add(new DashboardMetric("Ingreso Hoy", formatter.format(ingresoHoy), 
                        android.R.drawable.ic_menu_sort_by_size,
                        R.drawable.bg_gradient_metric_income, "INGRESOS"));
                    metrics.add(new DashboardMetric("Total Pedidos", String.valueOf(pedidos.size()), 
                        android.R.drawable.ic_menu_directions,
                        R.drawable.bg_metric_reps, "TOTAL"));
                } else {
                    // Métricas por defecto si hay error
                    metrics.add(new DashboardMetric("Pendientes", "0", 
                        android.R.drawable.ic_menu_recent_history,
                        R.drawable.bg_gradient_metric_pending, "PENDIENTE"));
                    metrics.add(new DashboardMetric("Ingreso Hoy", "Bs. 0", 
                        android.R.drawable.ic_menu_sort_by_size,
                        R.drawable.bg_gradient_metric_income, "INGRESOS"));
                    metrics.add(new DashboardMetric("Total Pedidos", "0", 
                        android.R.drawable.ic_menu_directions,
                        R.drawable.bg_metric_reps, "TOTAL"));
                }
                
                metricAdapter = new MetricAdapter(metrics, metric -> {
                    if (metric.getFilterType().equals("PENDIENTE")) {
                        Bundle args = new Bundle();
                        args.putString("filter", "Pendiente");
                        Navigation.findNavController(requireView()).navigate(R.id.pedidosListFragment, args);
                    }
                });
                rvMetrics.setAdapter(metricAdapter);
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                // Métricas por defecto en caso de error
                List<DashboardMetric> metrics = new ArrayList<>();
                metrics.add(new DashboardMetric("Pendientes", "0", 
                    android.R.drawable.ic_menu_recent_history,
                    R.drawable.bg_gradient_metric_pending, "PENDIENTE"));
                metrics.add(new DashboardMetric("Ingreso Hoy", "Bs. 0", 
                    android.R.drawable.ic_menu_sort_by_size,
                    R.drawable.bg_gradient_metric_income, "INGRESOS"));
                metrics.add(new DashboardMetric("Total Pedidos", "0", 
                    android.R.drawable.ic_menu_directions,
                    R.drawable.bg_metric_reps, "TOTAL"));
                
                metricAdapter = new MetricAdapter(metrics, metric -> {
                    if (metric.getFilterType().equals("PENDIENTE")) {
                        Bundle args = new Bundle();
                        args.putString("filter", "Pendiente");
                        Navigation.findNavController(requireView()).navigate(R.id.pedidosListFragment, args);
                    }
                });
                rvMetrics.setAdapter(metricAdapter);
            }
        });
    }

    private List<Module> getModules() {
        List<Module> modules = new ArrayList<>();

        // Leer tipo de usuario de SharedPreferences
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs",
                android.content.Context.MODE_PRIVATE);
        String tipoUsuario = prefs.getString("user_tipo", "Cliente");

        // Módulos comunes para todos
        modules.add(new Module("Órdenes y Tracking", R.drawable.ic_route, R.drawable.bg_card_orders,
                "pedidosListFragment"));
        modules.add(new Module("Catálogo e Inventario", R.drawable.ic_inventory, R.drawable.bg_card_catalog,
                "productCatalogFragment"));

        // Módulos específicos según tipo de usuario
        if ("Empresa".equals(tipoUsuario) || "Administrador".equals(tipoUsuario)) {
            // Solo empresas y administradores ven estos módulos administrativos
            modules.add(new Module("Planes y Perfiles", R.drawable.ic_storefront, R.drawable.bg_card_profiles,
                    "planesYPerfilesFragment"));
            modules.add(new Module("Informes y Métricas", R.drawable.ic_bar_chart, R.drawable.bg_gradient_reports,
                    "nav_reports"));
        }

        // Configuración visible para todos
        modules.add(
                new Module("Configuración", R.drawable.ic_settings, R.drawable.bg_gradient_settings, "nav_settings"));

        return modules;
    }
}
