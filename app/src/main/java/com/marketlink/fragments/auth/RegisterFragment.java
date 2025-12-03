package com.marketlink.fragments.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.marketlink.R;

public class RegisterFragment extends Fragment {

    private TextInputEditText etNombreCompleto;
    private TextInputEditText etEmail;
    private TextInputEditText etTelefono;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputEditText etRazonSocial;
    private TextInputEditText etNit;
    private TextInputEditText etDireccion;
    private android.widget.AutoCompleteTextView actvCiudad;
    private TextInputEditText etDireccionCliente;
    private RadioGroup rgTipoUsuario;
    private RadioButton rbEmprendedor;
    private RadioButton rbConsumidor;
    private MaterialButton btnRegister;
    private View llEmpresaFields;
    private View llClienteFields;
    private List<com.marketlink.models.Ciudad> ciudades = new ArrayList<>();
    private String ciudadIdSeleccionada = null;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        etNombreCompleto = view.findViewById(R.id.et_nombre_completo);
        etEmail = view.findViewById(R.id.et_email);
        etTelefono = view.findViewById(R.id.et_telefono);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        etRazonSocial = view.findViewById(R.id.et_razon_social);
        etNit = view.findViewById(R.id.et_nit);
        etDireccion = view.findViewById(R.id.et_direccion);
        actvCiudad = view.findViewById(R.id.actv_ciudad);
        etDireccionCliente = view.findViewById(R.id.et_direccion_cliente);
        rgTipoUsuario = view.findViewById(R.id.rg_tipo_usuario);
        rbEmprendedor = view.findViewById(R.id.rb_emprendedor);
        rbConsumidor = view.findViewById(R.id.rb_consumidor);
        btnRegister = view.findViewById(R.id.btn_register);
        llEmpresaFields = view.findViewById(R.id.ll_empresa_fields);
        llClienteFields = view.findViewById(R.id.ll_cliente_fields);
        
        // Cargar ciudades
        cargarCiudades();

        // Por defecto seleccionar Consumidor
        rbConsumidor.setChecked(true);

        // Mostrar/ocultar campos según selección
        rgTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_emprendedor) {
                llEmpresaFields.setVisibility(View.VISIBLE);
                llClienteFields.setVisibility(View.GONE);
            } else {
                llEmpresaFields.setVisibility(View.GONE);
                llClienteFields.setVisibility(View.VISIBLE);
            }
        });

        // Manejar click del botón de registro
        btnRegister.setOnClickListener(v -> {
            if (validarFormulario()) {
                registrarUsuario();
            }
        });

        // Navegar de vuelta al login
        view.findViewById(R.id.tv_login).setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });
    }

    private boolean validarFormulario() {
        String nombreCompleto = etNombreCompleto.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (nombreCompleto.isEmpty()) {
            etNombreCompleto.setError("Ingrese su nombre completo");
            etNombreCompleto.requestFocus();
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingrese un email válido");
            etEmail.requestFocus();
            return false;
        }

        if (telefono.isEmpty()) {
            etTelefono.setError("Ingrese su teléfono");
            etTelefono.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (rgTipoUsuario.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Seleccione el tipo de usuario", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registrarUsuario() {
        String nombreCompleto = etNombreCompleto.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString();

        String nombre = nombreCompleto;
        String apellido = "";

        if (nombreCompleto.contains(" ")) {
            int lastSpaceIndex = nombreCompleto.lastIndexOf(" ");
            nombre = nombreCompleto.substring(0, lastSpaceIndex);
            apellido = nombreCompleto.substring(lastSpaceIndex + 1);
        }

        // Determinar tipo de usuario basado en el RadioButton seleccionado
        boolean isEmpresa = rbEmprendedor.isChecked();

        com.marketlink.network.ApiService apiService = com.marketlink.network.ApiClient.getClient()
                .create(com.marketlink.network.ApiService.class);

        retrofit2.Call<com.marketlink.models.AuthResponse> call;
        
        if (isEmpresa) {
            // Validar campos de empresa
            String razonSocial = etRazonSocial.getText().toString().trim();
            String nit = etNit.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            
            if (razonSocial.isEmpty()) {
                etRazonSocial.setError("Ingrese la razón social");
                etRazonSocial.requestFocus();
                return;
            }
            
            if (nit.isEmpty()) {
                etNit.setError("Ingrese el NIT");
                etNit.requestFocus();
                return;
            }
            
            if (direccion.isEmpty()) {
                etDireccion.setError("Ingrese la dirección");
                etDireccion.requestFocus();
                return;
            }
            
            // Registrar empresa
            com.marketlink.models.RegistroEmpresaRequest registroEmpresaRequest = 
                new com.marketlink.models.RegistroEmpresaRequest(
                    email, password, nombre, apellido, telefono, razonSocial, nit, direccion);
            call = apiService.registroEmpresa(registroEmpresaRequest);
        } else {
            // Validar ciudad para cliente
            if (ciudadIdSeleccionada == null || ciudadIdSeleccionada.isEmpty()) {
                Toast.makeText(getContext(), "Debe seleccionar una ciudad", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String direccionCliente = etDireccionCliente.getText() != null ? 
                etDireccionCliente.getText().toString().trim() : null;
            
            // Registrar cliente
            com.marketlink.models.RegistroRequest registroRequest = 
                new com.marketlink.models.RegistroRequest(
                    email, password, nombre, apellido, telefono, "Cliente", 
                    ciudadIdSeleccionada, direccionCliente);
            call = apiService.registro(registroRequest);
        }
        call.enqueue(new retrofit2.Callback<com.marketlink.models.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.marketlink.models.AuthResponse> call,
                    retrofit2.Response<com.marketlink.models.AuthResponse> response) {
                // Verificar que el fragment esté adjunto antes de continuar
                if (!isAdded() || getActivity() == null || getContext() == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    com.marketlink.models.AuthResponse authResponse = response.body();

                    // Guardar información del usuario en SharedPreferences
                    SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("auth_token", authResponse.getToken());
                    editor.putString("user_id", authResponse.getUsuarioId());
                    editor.putString("user_email", authResponse.getEmail());
                    editor.putString("user_name", authResponse.getNombre());
                    editor.putString("user_tipo", authResponse.getRol());
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    // Mostrar mensaje de éxito
                    Toast.makeText(getContext(),
                            "¡Registro exitoso como " + authResponse.getRol() + "!",
                            Toast.LENGTH_LONG).show();

                    // Navegar según el tipo de usuario
                    View view = getView();
                    if (view != null && isAdded()) {
                        String rol = authResponse.getRol();
                        if ("Cliente".equals(rol)) {
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_registerFragment_to_homeFragment);
                        } else {
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_registerFragment_to_dashboardFragment);
                        }
                    }
                } else {
                    // Intentar leer el mensaje de error del body
                    String errorMessage = "Registro fallido";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("message")) {
                                // Parsear JSON simple
                                int messageIndex = errorBody.indexOf("\"message\"");
                                if (messageIndex != -1) {
                                    int start = errorBody.indexOf("\"", messageIndex + 10) + 1;
                                    int end = errorBody.indexOf("\"", start);
                                    if (end > start) {
                                        errorMessage = errorBody.substring(start, end);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        errorMessage = response.message() != null ? response.message() : "Error desconocido";
                    }
                    if (getContext() != null) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.marketlink.models.AuthResponse> call, Throwable t) {
                // Verificar que el fragment esté adjunto antes de mostrar el error
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarCiudades() {
        com.marketlink.network.ApiService apiService = com.marketlink.network.ApiClient.getClient()
                .create(com.marketlink.network.ApiService.class);
        
        retrofit2.Call<List<com.marketlink.models.Ciudad>> call = apiService.getCiudades();
        call.enqueue(new retrofit2.Callback<List<com.marketlink.models.Ciudad>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.marketlink.models.Ciudad>> call,
                    retrofit2.Response<List<com.marketlink.models.Ciudad>> response) {
                // Verificar que el fragment esté adjunto antes de continuar
                if (!isAdded() || getContext() == null || actvCiudad == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    ciudades = response.body();
                    // Filtrar solo ciudades activas
                    ciudades = ciudades.stream()
                        .filter(c -> c.getActiva() != null && c.getActiva())
                        .collect(java.util.stream.Collectors.toList());
                    
                    // Configurar AutoCompleteTextView
                    List<String> nombresCiudades = new ArrayList<>();
                    for (com.marketlink.models.Ciudad ciudad : ciudades) {
                        nombresCiudades.add(ciudad.getNombre());
                    }
                    
                    android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        nombresCiudades
                    );
                    actvCiudad.setAdapter(adapter);
                    
                    actvCiudad.setOnItemClickListener((parent, view, position, id) -> {
                        String nombreCiudad = (String) parent.getItemAtPosition(position);
                        ciudadIdSeleccionada = ciudades.stream()
                            .filter(c -> c.getNombre().equals(nombreCiudad))
                            .findFirst()
                            .map(com.marketlink.models.Ciudad::getId)
                            .orElse(null);
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.marketlink.models.Ciudad>> call, Throwable t) {
                // Verificar que el fragment esté adjunto antes de mostrar el error
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error al cargar ciudades: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
