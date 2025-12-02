package com.marketlink.fragments.usuarios;

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
import com.marketlink.adapters.UserAdapter;
import com.marketlink.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private FloatingActionButton fabAddUser;

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
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
                        R.id.dashboardFragment, R.id.pedidosListFragment, R.id.productCatalogFragment, 
                        R.id.empresaListFragment, R.id.userListFragment
                ).setOpenableLayout(drawerLayout).build();
                
                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        rvUsers = view.findViewById(R.id.rv_users);
        fabAddUser = view.findViewById(R.id.fab_add_user);

        // Setup RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get mock data
        List<User> users = getMockUsers();
        
        // Setup Adapter
        userAdapter = new UserAdapter(users, user -> {
            // Navigate to detail fragment
            Navigation.findNavController(view).navigate(R.id.action_userListFragment_to_userDetailFragment);
        });
        
        rvUsers.setAdapter(userAdapter);

        // FAB Click Listener
        fabAddUser.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Añadir Nuevo Usuario", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add user fragment
        });
    }

    private List<User> getMockUsers() {
        List<User> users = new ArrayList<>();
        
        // Administradores
        users.add(new User("1", "Carlos Mendoza", "carlos.mendoza@marketlink.bo", "77777777", 
                "Administrador", "Activo", "Administrador"));
        users.add(new User("2", "Ana García", "ana.garcia@marketlink.bo", "22222222", 
                "Administrador", "Activo", "Administrador"));
        
        // Repartidores
        users.add(new User("3", "Luis Fernández", "luis.fernandez@marketlink.bo", "33333333", 
                "Repartidor", "Activo", "Repartidor"));
        users.add(new User("4", "María López", "maria.lopez@marketlink.bo", "44444444", 
                "Repartidor", "Activo", "Repartidor"));
        users.add(new User("5", "Roberto Silva", "roberto.silva@marketlink.bo", "55555555", 
                "Repartidor", "Inactivo", "Repartidor"));
        
        // Clientes
        users.add(new User("6", "Pedro Martínez", "pedro.martinez@example.com", "66666666", 
                "Cliente", "Activo", "Consumidor"));
        users.add(new User("7", "Laura Sánchez", "laura.sanchez@example.com", "77777777", 
                "Cliente", "Activo", "Consumidor"));
        users.add(new User("8", "Diego Torres", "diego.torres@example.com", "88888888", 
                "Cliente", "Inactivo", "Consumidor"));
        
        return users;
    }
}

