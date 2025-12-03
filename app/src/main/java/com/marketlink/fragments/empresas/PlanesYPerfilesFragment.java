package com.marketlink.fragments.empresas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.marketlink.R;
import com.marketlink.adapters.PlanAdapter;
import com.marketlink.adapters.PerfilComercialGestionAdapter;
import com.marketlink.models.Ciudad;
import com.marketlink.models.Plan;
import com.marketlink.models.PerfilComercial;
import com.marketlink.models.SuscripcionEmpresa;
import com.marketlink.models.CreateSuscripcionRequest;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanesYPerfilesFragment extends Fragment {

    private TextView tvPlanActual;
    private TextView tvLimitePerfiles;
    private TextView tvPerfilesUsados;
    private TextView tvFechaVencimiento;
    private MaterialButton btnCambiarPlan;
    private RecyclerView rvPlanes;
    private RecyclerView rvPerfiles;
    private ProgressBar progressBar;
    private Plan planActual;
    private SuscripcionEmpresa suscripcionActual;
    private List<PerfilComercial> perfilesExistentes = new ArrayList<>();
    private List<Ciudad> ciudadesDisponibles = new ArrayList<>();
    private List<String> ciudadesOcupadas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planes_y_perfiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        }

        tvPlanActual = view.findViewById(R.id.tv_plan_actual);
        tvLimitePerfiles = view.findViewById(R.id.tv_limite_perfiles);
        tvPerfilesUsados = view.findViewById(R.id.tv_perfiles_usados);
        tvFechaVencimiento = view.findViewById(R.id.tv_fecha_vencimiento);
        btnCambiarPlan = view.findViewById(R.id.btn_cambiar_plan);
        rvPlanes = view.findViewById(R.id.rv_planes);
        rvPerfiles = view.findViewById(R.id.rv_perfiles);
        progressBar = view.findViewById(R.id.progress_bar);
        MaterialButton btnCrearPerfil = view.findViewById(R.id.btn_crear_perfil);

        rvPlanes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPerfiles.setLayoutManager(new LinearLayoutManager(getContext()));

        btnCambiarPlan.setOnClickListener(v -> mostrarDialogoCambiarPlan());
        btnCrearPerfil.setOnClickListener(v -> mostrarDialogoCrearPerfil());

        cargarDatos();
    }

    private void cargarDatos() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Cargar suscripción actual
        cargarSuscripcion();
        // Cargar perfiles
        cargarPerfiles();
        // Cargar ciudades
        cargarCiudades();
    }

    private void cargarSuscripcion() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SuscripcionEmpresa> call = apiService.getMiSuscripcion();
        
        call.enqueue(new Callback<SuscripcionEmpresa>() {
            @Override
            public void onResponse(Call<SuscripcionEmpresa> call, Response<SuscripcionEmpresa> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suscripcionActual = response.body();
                    cargarPlan(suscripcionActual.getPlanId());
                } else {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "No tienes una suscripción activa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SuscripcionEmpresa> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Error al cargar suscripción: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPlan(String planId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Plan> call = apiService.getPlan(planId);
        
        call.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    planActual = response.body();
                    actualizarUI();
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                android.util.Log.e("PlanesYPerfiles", "Error al cargar plan: " + t.getMessage());
            }
        });
    }

    private void cargarPerfiles() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<PerfilComercial>> call = apiService.getMisPerfiles();
        
        call.enqueue(new Callback<List<PerfilComercial>>() {
            @Override
            public void onResponse(Call<List<PerfilComercial>> call, Response<List<PerfilComercial>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    perfilesExistentes = response.body();
                    ciudadesOcupadas = perfilesExistentes.stream()
                        .filter(p -> p.getCiudadId() != null)
                        .map(PerfilComercial::getCiudadId)
                        .collect(Collectors.toList());
                    mostrarPerfiles();
                }
            }

            @Override
            public void onFailure(Call<List<PerfilComercial>> call, Throwable t) {
                android.util.Log.e("PlanesYPerfiles", "Error al cargar perfiles: " + t.getMessage());
            }
        });
    }

    private void cargarCiudades() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Ciudad>> call = apiService.getCiudades();
        
        call.enqueue(new Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ciudadesDisponibles = response.body().stream()
                        .filter(c -> c.getActiva() != null && c.getActiva())
                        .collect(Collectors.toList());
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                android.util.Log.e("PlanesYPerfiles", "Error al cargar ciudades: " + t.getMessage());
            }
        });
    }

    private void actualizarUI() {
        if (planActual != null && suscripcionActual != null) {
            tvPlanActual.setText(planActual.getNombre());
            tvLimitePerfiles.setText(String.valueOf(planActual.getMaxPerfilesComerciales()));
            tvPerfilesUsados.setText(String.valueOf(perfilesExistentes.size()));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvFechaVencimiento.setText(sdf.format(suscripcionActual.getFechaFin()));
            
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarPerfiles() {
        PerfilComercialGestionAdapter adapter = new PerfilComercialGestionAdapter(perfilesExistentes, 
            new PerfilComercialGestionAdapter.OnPerfilActionListener() {
                @Override
                public void onEditarPerfil(PerfilComercial perfil) {
                    mostrarDialogoEditarPerfil(perfil);
                }

                @Override
                public void onVerProductos(PerfilComercial perfil) {
                    Bundle args = new Bundle();
                    args.putString("perfil_id", perfil.getId());
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_planesYPerfilesFragment_to_productCatalogFragment, args);
                }
            });
        rvPerfiles.setAdapter(adapter);
        actualizarUI();
    }

    private void mostrarDialogoCambiarPlan() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Plan>> call = apiService.getPlanes();
        
        call.enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Plan> planes = response.body().stream()
                        .filter(p -> p.getActivo() != null && p.getActivo())
                        .collect(Collectors.toList());
                    
                    mostrarListaPlanes(planes);
                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar planes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarListaPlanes(List<Plan> planes) {
        PlanAdapter adapter = new PlanAdapter(planes, plan -> {
            cambiarPlan(plan.getId());
        });
        rvPlanes.setAdapter(adapter);
        
        // Mostrar diálogo con la lista
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_planes, null);
        RecyclerView rvPlanesDialog = dialogView.findViewById(R.id.rv_planes);
        rvPlanesDialog.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlanesDialog.setAdapter(adapter);
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar Plan")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void cambiarPlan(String planId) {
        progressBar.setVisibility(View.VISIBLE);
        CreateSuscripcionRequest request = new CreateSuscripcionRequest();
        request.setPlanId(planId);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SuscripcionEmpresa> call = apiService.createSuscripcion(request);
        
        call.enqueue(new Callback<SuscripcionEmpresa>() {
            @Override
            public void onResponse(Call<SuscripcionEmpresa> call, Response<SuscripcionEmpresa> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Plan cambiado exitosamente", Toast.LENGTH_SHORT).show();
                    cargarDatos();
                } else {
                    Toast.makeText(getContext(), "Error al cambiar plan: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SuscripcionEmpresa> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCrearPerfil() {
        // Verificar límite
        if (planActual != null && perfilesExistentes.size() >= planActual.getMaxPerfilesComerciales()) {
            Toast.makeText(getContext(), 
                "Has alcanzado el límite de perfiles permitidos por tu plan (" + 
                planActual.getMaxPerfilesComerciales() + ")", 
                Toast.LENGTH_LONG).show();
            return;
        }

        // Filtrar ciudades disponibles (excluir las ocupadas)
        List<Ciudad> ciudadesDisponiblesParaPerfil = ciudadesDisponibles.stream()
            .filter(c -> !ciudadesOcupadas.contains(c.getId()))
            .collect(Collectors.toList());

        if (ciudadesDisponiblesParaPerfil.isEmpty()) {
            Toast.makeText(getContext(), "No hay ciudades disponibles. Ya tienes perfiles en todas las ciudades.", Toast.LENGTH_LONG).show();
            return;
        }

        // Crear diálogo para seleccionar ciudad y crear perfil
        String[] ciudadesNombres = ciudadesDisponiblesParaPerfil.stream()
            .map(Ciudad::getNombre)
            .toArray(String[]::new);

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Crear Nuevo Perfil Comercial")
            .setItems(ciudadesNombres, (dialog, which) -> {
                Ciudad ciudadSeleccionada = ciudadesDisponiblesParaPerfil.get(which);
                crearPerfil(ciudadSeleccionada.getId());
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void crearPerfil(String ciudadId) {
        // Mostrar diálogo para crear perfil básico
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crear_perfil, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.et_nombre);
        android.widget.EditText etDescripcion = dialogView.findViewById(R.id.et_descripcion);
        android.widget.EditText etDireccion = dialogView.findViewById(R.id.et_direccion);
        android.widget.EditText etTelefono = dialogView.findViewById(R.id.et_telefono);
        android.widget.EditText etEmail = dialogView.findViewById(R.id.et_email);
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Crear Perfil Comercial")
            .setView(dialogView)
            .setPositiveButton("Crear", (dialog, which) -> {
                PerfilComercial nuevoPerfil = new PerfilComercial();
                nuevoPerfil.setNombre(etNombre.getText().toString());
                nuevoPerfil.setDescripcion(etDescripcion.getText().toString());
                nuevoPerfil.setDireccion(etDireccion.getText().toString());
                nuevoPerfil.setTelefono(etTelefono.getText().toString());
                nuevoPerfil.setEmail(etEmail.getText().toString());
                nuevoPerfil.setCiudadId(ciudadId);
                nuevoPerfil.setActivo(true);
                
                crearPerfilEnServidor(nuevoPerfil);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void crearPerfilEnServidor(PerfilComercial perfil) {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PerfilComercial> call = apiService.createPerfil(perfil);
        
        call.enqueue(new Callback<PerfilComercial>() {
            @Override
            public void onResponse(Call<PerfilComercial> call, Response<PerfilComercial> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Perfil creado exitosamente", Toast.LENGTH_SHORT).show();
                    cargarPerfiles();
                } else {
                    String errorMsg = "Error al crear perfil";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg = response.message();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilComercial> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoEditarPerfil(PerfilComercial perfil) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crear_perfil, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.et_nombre);
        android.widget.EditText etDescripcion = dialogView.findViewById(R.id.et_descripcion);
        android.widget.EditText etDireccion = dialogView.findViewById(R.id.et_direccion);
        android.widget.EditText etTelefono = dialogView.findViewById(R.id.et_telefono);
        android.widget.EditText etEmail = dialogView.findViewById(R.id.et_email);
        
        // Llenar campos con datos existentes
        etNombre.setText(perfil.getNombre());
        etDescripcion.setText(perfil.getDescripcion());
        etDireccion.setText(perfil.getDireccion());
        etTelefono.setText(perfil.getTelefono());
        etEmail.setText(perfil.getEmail());
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Perfil Comercial")
            .setView(dialogView)
            .setPositiveButton("Guardar", (dialog, which) -> {
                perfil.setNombre(etNombre.getText().toString());
                perfil.setDescripcion(etDescripcion.getText().toString());
                perfil.setDireccion(etDireccion.getText().toString());
                perfil.setTelefono(etTelefono.getText().toString());
                perfil.setEmail(etEmail.getText().toString());
                
                actualizarPerfilEnServidor(perfil);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void actualizarPerfilEnServidor(PerfilComercial perfil) {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PerfilComercial> call = apiService.updatePerfil(perfil.getId(), perfil);
        
        call.enqueue(new Callback<PerfilComercial>() {
            @Override
            public void onResponse(Call<PerfilComercial> call, Response<PerfilComercial> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    cargarPerfiles();
                } else {
                    String errorMsg = "Error al actualizar perfil";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg = response.message();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilComercial> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

