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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marketlink.MainActivity;
import com.marketlink.R;
import com.marketlink.models.User;

public class UserDetailFragment extends Fragment {

    private TextInputEditText etNombre;
    private TextInputEditText etEmail;
    private TextInputEditText etTelefono;
    private TextInputEditText etRol;
    private TextInputLayout tilRol;
    private MaterialButton btnDesactivar;
    private MaterialButton btnRestablecerPassword;
    private MaterialButton btnGuardar;
    
    private User currentUser;
    private BottomSheetDialog roleBottomSheet;

    public UserDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            
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

        // Initialize views
        etNombre = view.findViewById(R.id.et_nombre);
        etEmail = view.findViewById(R.id.et_email);
        etTelefono = view.findViewById(R.id.et_telefono);
        etRol = view.findViewById(R.id.et_rol);
        tilRol = view.findViewById(R.id.til_rol);
        btnDesactivar = view.findViewById(R.id.btn_desactivar);
        btnRestablecerPassword = view.findViewById(R.id.btn_restablecer_password);
        btnGuardar = view.findViewById(R.id.btn_guardar);

        // Get user from arguments (mock for now)
        currentUser = getMockUser();

        // Populate fields
        if (currentUser != null) {
            etNombre.setText(currentUser.getNombreCompleto());
            etEmail.setText(currentUser.getEmail());
            etTelefono.setText(currentUser.getTelefono());
            etRol.setText(currentUser.getRol());
        }

        // Setup Role Selector BottomSheet
        setupRoleSelector();

        // Setup listeners
        etRol.setOnClickListener(v -> {
            if (roleBottomSheet != null) {
                roleBottomSheet.show();
            }
        });

        tilRol.setEndIconOnClickListener(v -> {
            if (roleBottomSheet != null) {
                roleBottomSheet.show();
            }
        });

        btnDesactivar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Desactivar cuenta", Toast.LENGTH_SHORT).show();
            // TODO: Implement deactivate logic
        });

        btnRestablecerPassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Restablecer contraseña", Toast.LENGTH_SHORT).show();
            // TODO: Implement password reset logic
        });

        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Guardar cambios", Toast.LENGTH_SHORT).show();
            // TODO: Implement save logic
            Navigation.findNavController(view).navigateUp();
        });
    }

    private void setupRoleSelector() {
        roleBottomSheet = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_role_selector, null);
        roleBottomSheet.setContentView(sheetView);

        MaterialButton btnRolAdmin = sheetView.findViewById(R.id.btn_rol_admin);
        MaterialButton btnRolRepartidor = sheetView.findViewById(R.id.btn_rol_repartidor);
        MaterialButton btnRolCliente = sheetView.findViewById(R.id.btn_rol_cliente);

        // Configure stroke colors programmatically
        int strokeWidth = (int) (2 * getResources().getDisplayMetrics().density); // 2dp in pixels
        if (btnRolAdmin != null) {
            btnRolAdmin.setStrokeColorResource(R.color.colorPrimary);
            btnRolAdmin.setStrokeWidth(strokeWidth);
        }
        if (btnRolRepartidor != null) {
            btnRolRepartidor.setStrokeColorResource(R.color.colorAccent);
            btnRolRepartidor.setStrokeWidth(strokeWidth);
        }
        if (btnRolCliente != null) {
            btnRolCliente.setStrokeColorResource(R.color.colorSecondary);
            btnRolCliente.setStrokeWidth(strokeWidth);
        }

        btnRolAdmin.setOnClickListener(v -> {
            etRol.setText("Administrador");
            roleBottomSheet.dismiss();
        });

        btnRolRepartidor.setOnClickListener(v -> {
            etRol.setText("Repartidor");
            roleBottomSheet.dismiss();
        });

        btnRolCliente.setOnClickListener(v -> {
            etRol.setText("Cliente");
            roleBottomSheet.dismiss();
        });
    }

    private User getMockUser() {
        // In a real app, this would come from navigation arguments
        return new User("1", "Carlos Mendoza", "carlos.mendoza@marketlink.bo", 
                "77777777", "Administrador", "Activo","Emprendedor");
    }
}

