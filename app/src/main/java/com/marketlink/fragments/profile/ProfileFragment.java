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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.marketlink.MainActivity;
import com.marketlink.R;
import com.marketlink.models.PerfilComercial;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private TextView tvRole;
    private TextView tvEmail;
    private TextView tvPhone;
    private ImageView ivAvatar;
    private MaterialCardView cardChangePassword;
    private MaterialCardView cardChangePerfilComercial;
    private TextView tvPerfilActivo;
    private MaterialButton btnLogout;
    private List<PerfilComercial> perfilesComerciales;

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
        cardChangePerfilComercial = view.findViewById(R.id.card_change_perfil_comercial);
        tvPerfilActivo = view.findViewById(R.id.tv_perfil_activo);
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

        // Mostrar opción de cambiar perfil comercial solo para empresas
        if ("Empresa".equals(tipoUsuario)) {
            cardChangePerfilComercial.setVisibility(View.VISIBLE);
            actualizarPerfilActivo();
            cargarPerfilesComerciales();
        } else {
            cardChangePerfilComercial.setVisibility(View.GONE);
        }

        // Change password card click
        cardChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Redirigiendo a cambiar contraseña...", Toast.LENGTH_SHORT).show();
            // Navigate to configuration fragment
            Navigation.findNavController(view).navigate(R.id.configuracionFragment);
        });

        // Change perfil comercial card click
        cardChangePerfilComercial.setOnClickListener(v -> {
            mostrarDialogoSeleccionarPerfil();
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

    private void cargarPerfilesComerciales() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<PerfilComercial>> call = apiService.getMisPerfiles();
        
        call.enqueue(new Callback<List<PerfilComercial>>() {
            @Override
            public void onResponse(Call<List<PerfilComercial>> call, Response<List<PerfilComercial>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    perfilesComerciales = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<PerfilComercial>> call, Throwable t) {
                android.util.Log.e("ProfileFragment", "Error al cargar perfiles: " + t.getMessage());
            }
        });
    }

    private void mostrarDialogoSeleccionarPerfil() {
        if (perfilesComerciales == null || perfilesComerciales.isEmpty()) {
            Toast.makeText(getContext(), "No tienes perfiles comerciales. Ve a Planes y Perfiles para crear uno.", Toast.LENGTH_LONG).show();
            return;
        }

        String[] nombresPerfiles = perfilesComerciales.stream()
            .map(p -> p.getNombre() != null ? p.getNombre() : "Sin nombre")
            .toArray(String[]::new);

        // Obtener perfil actualmente seleccionado
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String perfilActualId = prefs.getString("perfil_comercial_seleccionado_id", null);
        
        int seleccionadoIndex = -1;
        if (perfilActualId != null) {
            for (int i = 0; i < perfilesComerciales.size(); i++) {
                if (perfilActualId.equals(perfilesComerciales.get(i).getId())) {
                    seleccionadoIndex = i;
                    break;
                }
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar Perfil Comercial Activo")
            .setSingleChoiceItems(nombresPerfiles, seleccionadoIndex, (dialog, which) -> {
                PerfilComercial perfilSeleccionado = perfilesComerciales.get(which);
                
                // Guardar perfil seleccionado en SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("perfil_comercial_seleccionado_id", perfilSeleccionado.getId());
                editor.putString("perfil_comercial_seleccionado_nombre", perfilSeleccionado.getNombre());
                editor.apply();
                
                Toast.makeText(getContext(), "Perfil comercial activo: " + perfilSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
                actualizarPerfilActivo();
                dialog.dismiss();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void actualizarPerfilActivo() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String perfilActivoNombre = prefs.getString("perfil_comercial_seleccionado_nombre", null);
        
        if (perfilActivoNombre != null && !perfilActivoNombre.isEmpty()) {
            tvPerfilActivo.setText("Activo: " + perfilActivoNombre);
        } else {
            tvPerfilActivo.setText("Ninguno seleccionado");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar perfil activo si es empresa
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String tipoUsuario = prefs.getString("user_tipo", "Cliente");
        if ("Empresa".equals(tipoUsuario) && tvPerfilActivo != null) {
            actualizarPerfilActivo();
        }
    }
}


