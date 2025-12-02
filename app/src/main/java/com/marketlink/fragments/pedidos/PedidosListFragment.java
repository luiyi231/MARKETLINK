package com.marketlink.fragments.pedidos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.marketlink.MainActivity;
import com.marketlink.R;
import com.marketlink.adapters.PedidoAdapter;
import com.marketlink.models.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedidosListFragment extends Fragment {

    private RecyclerView rvPedidos;
    private PedidoAdapter pedidoAdapter;
    private ChipGroup chipGroupFilters;
    private List<Pedido> allPedidos;
    private String currentFilter = "Todos";

    public PedidosListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedidos_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        rvPedidos = view.findViewById(R.id.rv_pedidos);
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);

        rvPedidos.setLayoutManager(new LinearLayoutManager(getContext()));

        allPedidos = new ArrayList<>();
        pedidoAdapter = new PedidoAdapter(allPedidos, new PedidoAdapter.OnPedidoActionListener() {
            @Override
            public void onPedidoClick(Pedido pedido) {
                Toast.makeText(getContext(), "Pedido #" + pedido.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCallClick(Pedido pedido) {
                // Simulate phone call
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+59177777777"));
                startActivity(intent);
            }

            @Override
            public void onMapClick(Pedido pedido) {
                // Navegar al fragment de tracking con información del pedido
                Bundle args = new Bundle();
                args.putString("pedido_id", pedido.getId());
                args.putString("pedido_cliente", pedido.getClienteId());
                args.putString("pedido_status", pedido.getEstado());
                Navigation.findNavController(requireView()).navigate(R.id.trackingFragment, args);
            }
        });

        rvPedidos.setAdapter(pedidoAdapter);

        // Setup filter chips
        setupFilterChips();

        // Check if we have a filter argument from navigation
        if (getArguments() != null && getArguments().containsKey("filter")) {
            String filter = getArguments().getString("filter");
            applyFilter(filter);
            selectChipByFilter(filter);
        }

        // Cargar pedidos desde el backend
        loadPedidos();
    }

    private void loadPedidos() {
        android.content.Context context = getContext();
        if (context == null) return;

        android.content.SharedPreferences prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        
        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Debe iniciar sesión para ver pedidos", Toast.LENGTH_SHORT).show();
            return;
        }

        com.marketlink.network.ApiService apiService = com.marketlink.network.ApiClient.getClient().create(com.marketlink.network.ApiService.class);
        // El token se agrega automáticamente por el interceptor
        retrofit2.Call<java.util.List<Pedido>> call = apiService.getMisPedidos();
        
        call.enqueue(new retrofit2.Callback<java.util.List<Pedido>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<Pedido>> call, retrofit2.Response<java.util.List<Pedido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPedidos = response.body();
                    applyFilter(currentFilter);
                } else {
                    Toast.makeText(getContext(), "Error al cargar pedidos: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<Pedido>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterChips() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip selectedChip = group.findViewById(checkedId);
                if (selectedChip != null) {
                    String filter = selectedChip.getText().toString();
                    applyFilter(filter);
                }
            }
        });
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        List<Pedido> filteredList;

        if (filter.equals("Todos")) {
            filteredList = allPedidos;
        } else {
            filteredList = allPedidos.stream()
                    .filter(pedido -> pedido.getEstado() != null && pedido.getEstado().equals(filter))
                    .collect(Collectors.toList());
        }

        pedidoAdapter.updateList(filteredList);
    }

    private void selectChipByFilter(String filter) {
        if (filter.equals("Pendiente")) {
            chipGroupFilters.check(R.id.chip_pending);
        } else if (filter.equals("En Curso") || filter.equals("En Ruta")) {
            chipGroupFilters.check(R.id.chip_in_progress);
        } else if (filter.equals("Entregado")) {
            chipGroupFilters.check(R.id.chip_delivered);
        } else {
            chipGroupFilters.check(R.id.chip_all);
        }
    }

}
