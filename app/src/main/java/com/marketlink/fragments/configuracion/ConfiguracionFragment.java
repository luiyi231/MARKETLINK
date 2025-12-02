package com.marketlink.fragments.configuracion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.marketlink.MainActivity;
import com.marketlink.R;

public class ConfiguracionFragment extends Fragment {

    private SwitchMaterial switchDarkMode;
    private View cardNotifications;
    private View cardPassword;
    private MaterialButton btnLogout;

    public ConfiguracionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuracion, container, false);
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
                        R.id.empresaListFragment, R.id.userListFragment, R.id.configuracionFragment
                ).setOpenableLayout(drawerLayout).build();
                
                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        // Initialize views
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        cardNotifications = view.findViewById(R.id.card_notifications);
        cardPassword = view.findViewById(R.id.card_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Load saved dark mode preference
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);

        // Dark mode switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference first
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            // Apply theme change immediately
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(getContext(), "Modo oscuro activado", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(getContext(), "Modo oscuro desactivado", Toast.LENGTH_SHORT).show();
            }
            
            // Restart activity to apply theme changes completely
            if (getActivity() != null) {
                getActivity().recreate();
            }
        });

        // Notifications option
        cardNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Gestionar alertas", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to notifications settings
        });

        // Change password option
        cardPassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cambiar contraseña", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to change password screen
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            // TODO: Implement logout logic and navigate to login
            Navigation.findNavController(view).navigate(R.id.loginFragment);
        });
    }
}

