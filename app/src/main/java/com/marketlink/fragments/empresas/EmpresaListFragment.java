package com.marketlink.fragments.empresas;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marketlink.MainActivity;
import com.marketlink.R;
import com.marketlink.adapters.EmpresaAdapter;
import com.marketlink.models.Empresa;

import java.util.ArrayList;
import java.util.List;

public class EmpresaListFragment extends Fragment {

    private RecyclerView rvEmpresas;
    private EmpresaAdapter empresaAdapter;
    private FloatingActionButton fabAddEmpresa;

    public EmpresaListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empresa_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar with Navigation Drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            
            // Get drawer layout from MainActivity
            DrawerLayout drawerLayout = 
                ((MainActivity) getActivity()).getDrawerLayout();
            
            if (drawerLayout != null) {
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.dashboardFragment, R.id.pedidosListFragment, R.id.productCatalogFragment, R.id.empresaListFragment
                ).setOpenableLayout(drawerLayout).build();
                
                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        rvEmpresas = view.findViewById(R.id.rv_empresas);
        fabAddEmpresa = view.findViewById(R.id.fab_add_empresa);

        // Setup RecyclerView
        rvEmpresas.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get mock data
        List<Empresa> empresas = getMockEmpresas();
        
        // Setup Adapter
        empresaAdapter = new EmpresaAdapter(empresas, new EmpresaAdapter.OnEmpresaActionListener() {
            @Override
            public void onEmpresaClick(Empresa empresa) {
                Toast.makeText(getContext(), "Empresa: " + empresa.getRazonSocial(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to detail fragment
            }

            @Override
            public void onEditClick(Empresa empresa) {
                Toast.makeText(getContext(), "Editar: " + empresa.getRazonSocial(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to edit fragment
            }

            @Override
            public void onBlockClick(Empresa empresa) {
                Toast.makeText(getContext(), "Bloquear: " + empresa.getRazonSocial(), Toast.LENGTH_SHORT).show();
                // TODO: Implement block logic
            }

            @Override
            public void onViewSucursalesClick(Empresa empresa) {
                Toast.makeText(getContext(), "Ver Sucursales: " + empresa.getRazonSocial(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to sucursales fragment
            }
        });
        
        rvEmpresas.setAdapter(empresaAdapter);

        // FAB Click Listener
        fabAddEmpresa.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Añadir Nueva Empresa", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add empresa fragment
        });
    }

    private List<Empresa> getMockEmpresas() {
        List<Empresa> empresas = new ArrayList<>();
        
        // Create mock empresas using the correct constructor
        Empresa e1 = new Empresa();
        e1.setId("1");
        e1.setRazonSocial("TechSolutions Bolivia S.A.");
        e1.setNit("1234567890123");
        e1.setTelefono("77777777");
        e1.setEmail("contacto@techsolutions.bo");
        e1.setActiva(true);
        empresas.add(e1);
        
        Empresa e2 = new Empresa();
        e2.setId("2");
        e2.setRazonSocial("Comercial La Paz S.R.L.");
        e2.setNit("9876543210987");
        e2.setTelefono("22222222");
        e2.setEmail("info@comerciallp.bo");
        e2.setActiva(true);
        empresas.add(e2);
        
        Empresa e3 = new Empresa();
        e3.setId("3");
        e3.setRazonSocial("Distribuidora Nacional");
        e3.setNit("1122334455667");
        e3.setTelefono("33333333");
        e3.setEmail("ventas@distribuidora.bo");
        e3.setActiva(true);
        empresas.add(e3);
        
        Empresa e4 = new Empresa();
        e4.setId("4");
        e4.setRazonSocial("Importadora Andina");
        e4.setNit("9988776655443");
        e4.setTelefono("44444444");
        e4.setEmail("admin@importadora.bo");
        e4.setActiva(false);
        empresas.add(e4);
        
        Empresa e5 = new Empresa();
        e5.setId("5");
        e5.setRazonSocial("Retail Express");
        e5.setNit("5544332211009");
        e5.setTelefono("55555555");
        e5.setEmail("contacto@retailexpress.bo");
        e5.setActiva(false);
        empresas.add(e5);
        
        Empresa e6 = new Empresa();
        e6.setId("6");
        e6.setRazonSocial("Servicios Generales S.A.");
        e6.setNit("6677889900112");
        e6.setTelefono("66666666");
        e6.setEmail("info@serviciosgen.bo");
        e6.setActiva(false);
        empresas.add(e6);
        
        return empresas;
    }
}

