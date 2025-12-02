package com.marketlink.fragments.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.card.MaterialCardView;
import com.marketlink.MainActivity;
import com.marketlink.R;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private TextView tvRole;
    private TextView tvEmail;
    private TextView tvPhone;
    private ImageView ivAvatar;
    private MaterialCardView cardChangePassword;
    private MaterialButton btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
                        R.id.empresaListFragment, R.id.userListFragment, R.id.reportesFragment,
                        R.id.configuracionFragment, R.id.profileFragment
                ).setOpenableLayout(drawerLayout).build();
                
                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        // Initialize views
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvRole = view.findViewById(R.id.tv_role);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        cardChangePassword = view.findViewById(R.id.card_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Leer datos del usuario desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userName = prefs.getString("user_nombre", "Usuario");
        String userEmail = prefs.getString("user_email", "email@ejemplo.com");
        String userPhone = prefs.getString("user_telefono", "No disponible");
        String tipoUsuario = prefs.getString("user_tipo", "Cliente");
        
        // Determinar el rol basado en el tipo de usuario
        String userRole;
        if ("Empresa".equals(tipoUsuario)) {
            userRole = "Empresa";
        } else if ("Administrador".equals(tipoUsuario)) {
            userRole = "Administrador";
        } else {
            userRole = "Cliente";
        }

        // Set user data
        tvName.setText(userName);
        tvRole.setText(userRole);
        tvEmail.setText(userEmail);
        tvPhone.setText(userPhone);

        // Set avatar background based on tipo de usuario
        if ("Empresa".equals(tipoUsuario) || "Administrador".equals(tipoUsuario)) {
            ivAvatar.setBackgroundResource(R.drawable.bg_avatar_admin); // Color para emprendedor
        } else {
            ivAvatar.setBackgroundResource(R.drawable.bg_avatar_admin); // Por ahora mismo color, puedes cambiar después
        }

        // Change password card click
        cardChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Redirigiendo a cambiar contraseña...", Toast.LENGTH_SHORT).show();
            // Navigate to configuration fragment
            Navigation.findNavController(view).navigate(R.id.configuracionFragment);
        });

        // Logout button click
        btnLogout.setOnClickListener(v -> {
            // Limpiar SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            
            // Show confirmation toast
            Toast.makeText(getContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            
            // Navigate to login fragment and clear back stack
            Navigation.findNavController(view).navigate(
                R.id.action_profileFragment_to_loginFragment
            );
        });
    }
}


