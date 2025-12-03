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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marketlink.R;
import com.marketlink.adapters.ProductAdapter;
import com.marketlink.models.PerfilComercial;
import com.marketlink.models.Producto;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaDetailFragment extends Fragment implements OnMapReadyCallback {

    private TextView tvNombre;
    private TextView tvDescripcion;
    private TextView tvDireccion;
    private TextView tvTelefono;
    private TextView tvEmail;
    private TextView tvHorario;
    private RecyclerView rvProductos;
    private ProgressBar progressBar;
    private String perfilId;
    private GoogleMap googleMap;
    private PerfilComercial perfilComercial;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empresa_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener perfil_id de los argumentos
        if (getArguments() != null) {
            perfilId = getArguments().getString("perfil_id");
        }

        if (perfilId == null || perfilId.isEmpty()) {
            Toast.makeText(getContext(), "Error: No se proporcionó el ID del perfil", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        }

        tvNombre = view.findViewById(R.id.tv_nombre);
        tvDescripcion = view.findViewById(R.id.tv_descripcion);
        tvDireccion = view.findViewById(R.id.tv_direccion);
        tvTelefono = view.findViewById(R.id.tv_telefono);
        tvEmail = view.findViewById(R.id.tv_email);
        tvHorario = view.findViewById(R.id.tv_horario);
        rvProductos = view.findViewById(R.id.rv_productos);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup RecyclerView
        rvProductos.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Setup Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
            .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Cargar datos
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PerfilComercial> call = apiService.getPerfil(perfilId);

        call.enqueue(new Callback<PerfilComercial>() {
            @Override
            public void onResponse(Call<PerfilComercial> call, Response<PerfilComercial> response) {
                if (response.isSuccessful() && response.body() != null) {
                    perfilComercial = response.body();
                    mostrarDatosPerfil();
                    cargarProductos();
                } else {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "Error al cargar información de la empresa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilComercial> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatosPerfil() {
        if (perfilComercial == null) return;

        tvNombre.setText(perfilComercial.getNombre() != null ? perfilComercial.getNombre() : "Sin nombre");
        tvDescripcion.setText(perfilComercial.getDescripcion() != null ? perfilComercial.getDescripcion() : "Sin descripción");
        tvDireccion.setText(perfilComercial.getDireccion() != null ? perfilComercial.getDireccion() : "Sin dirección");
        tvTelefono.setText(perfilComercial.getTelefono() != null ? perfilComercial.getTelefono() : "N/A");
        tvEmail.setText(perfilComercial.getEmail() != null ? perfilComercial.getEmail() : "N/A");
        
        String horario = "";
        if (perfilComercial.getHorarioApertura() != null && perfilComercial.getHorarioCierre() != null) {
            horario = perfilComercial.getHorarioApertura() + " - " + perfilComercial.getHorarioCierre();
        }
        tvHorario.setText(horario.isEmpty() ? "Horario no disponible" : horario);

        // Actualizar mapa si está listo
        if (googleMap != null && perfilComercial.getLatitud() != null && perfilComercial.getLongitud() != null) {
            LatLng ubicacion = new LatLng(perfilComercial.getLatitud(), perfilComercial.getLongitud());
            googleMap.addMarker(new MarkerOptions().position(ubicacion).title(perfilComercial.getNombre()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f));
        }
    }

    private void cargarProductos() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Producto>> call = apiService.getProductosByPerfil(perfilId);

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body().stream()
                        .filter(p -> p.getDisponible() != null && p.getDisponible() && 
                                   p.getStock() != null && p.getStock() > 0)
                        .collect(java.util.stream.Collectors.toList());

                    mostrarProductos(productos);
                } else {
                    mostrarProductos(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Error al cargar productos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                mostrarProductos(new ArrayList<>());
            }
        });
    }

    private void mostrarProductos(List<Producto> productos) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String tipoUsuario = prefs.getString("user_tipo", "Cliente");

        productAdapter = new ProductAdapter(productos, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Producto producto) {
                // Navegar a detalle del producto
                Bundle args = new Bundle();
                args.putString("producto_id", producto.getId());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_empresaDetailFragment_to_productDetailFragment, args);
            }

            @Override
            public void onAddToCartClick(Producto producto) {
                if ("Cliente".equals(tipoUsuario)) {
                    com.marketlink.utils.CartManager.getInstance().addItem(producto, 1);
                    Toast.makeText(getContext(), "Agregado al carrito: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvProductos.setAdapter(productAdapter);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        
        // Si ya tenemos los datos del perfil, actualizar mapa
        if (perfilComercial != null && perfilComercial.getLatitud() != null && perfilComercial.getLongitud() != null) {
            LatLng ubicacion = new LatLng(perfilComercial.getLatitud(), perfilComercial.getLongitud());
            googleMap.addMarker(new MarkerOptions().position(ubicacion).title(perfilComercial.getNombre()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f));
        }
    }
}

