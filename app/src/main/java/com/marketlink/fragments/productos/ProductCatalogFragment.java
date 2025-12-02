package com.marketlink.fragments.productos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.marketlink.R;
import com.marketlink.adapters.ProductAdapter;
import com.marketlink.models.Producto;
import com.marketlink.models.PerfilComercial;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCatalogFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private FloatingActionButton fabAddProduct;
    private MaterialButton btnAddProductToolbar;
    private ProgressBar progressBar;
    private String tipoUsuario;
    private List<Producto> productos = new ArrayList<>();

    public ProductCatalogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Leer tipo de usuario
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        tipoUsuario = prefs.getString("user_tipo", "Cliente");
        
        android.util.Log.d("ProductCatalog", "=== INICIO DEBUG ===");
        android.util.Log.d("ProductCatalog", "Tipo de usuario: " + tipoUsuario);

        rvProducts = view.findViewById(R.id.rv_products);
        fabAddProduct = view.findViewById(R.id.fab_add_product);
        btnAddProductToolbar = view.findViewById(R.id.btn_add_product_toolbar);
        progressBar = view.findViewById(R.id.progress_bar);
        
        android.util.Log.d("ProductCatalog", "FAB encontrado: " + (fabAddProduct != null));
        if (fabAddProduct != null) {
            android.util.Log.d("ProductCatalog", "FAB visibility: " + fabAddProduct.getVisibility());
            android.util.Log.d("ProductCatalog", "FAB clickable: " + fabAddProduct.isClickable());
            android.util.Log.d("ProductCatalog", "FAB enabled: " + fabAddProduct.isEnabled());
        }

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Configurar visibilidad del botón de agregar al carrito
        boolean isCliente = "Cliente".equals(tipoUsuario);
        
        productAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Producto producto) {
                // Navegar a detalles del producto
                Bundle args = new Bundle();
                args.putString("producto_id", producto.getId());
                androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_productCatalogFragment_to_productDetailFragment, args);
            }

            @Override
            public void onAddToCartClick(Producto producto) {
                if ("Cliente".equals(tipoUsuario)) {
                    // Solo clientes pueden agregar al carrito
                    com.marketlink.utils.CartManager.getInstance().addItem(producto, 1);
                    Toast.makeText(getContext(), "Agregado al carrito: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                    
                    // Actualizar badge en MainActivity
                    if (getActivity() instanceof com.marketlink.MainActivity) {
                        ((com.marketlink.MainActivity) getActivity()).actualizarBadgeCarrito();
                    }
                } else {
                    // Empresas y administradores no pueden agregar al carrito
                    Toast.makeText(getContext(), "Esta función solo está disponible para clientes", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Configurar visibilidad del botón según tipo de usuario
        productAdapter.setShowAddToCartButton(isCliente);

        rvProducts.setAdapter(productAdapter);

        // Botón de agregar producto solo visible para empresas y administradores
        android.util.Log.d("ProductCatalog", "Verificando visibilidad botón agregar - tipoUsuario: " + tipoUsuario);
        if ("Empresa".equals(tipoUsuario) || "Administrador".equals(tipoUsuario)) {
            android.util.Log.d("ProductCatalog", "Usuario es Empresa/Admin - Mostrando botón");
            
            // Configurar botón en Toolbar (más confiable)
            if (btnAddProductToolbar != null) {
                btnAddProductToolbar.setVisibility(View.VISIBLE);
                btnAddProductToolbar.setOnClickListener(v -> {
                    android.util.Log.d("ProductCatalog", "=== BOTÓN TOOLBAR CLICKED ===");
                    Toast.makeText(getContext(), "Abriendo formulario...", Toast.LENGTH_SHORT).show();
                    handleFabClick();
                });
                android.util.Log.d("ProductCatalog", "Botón Toolbar configurado");
            }
            
            // También configurar FAB por si acaso
            if (fabAddProduct != null) {
                fabAddProduct.setVisibility(View.VISIBLE);
                fabAddProduct.setOnClickListener(v -> {
                    android.util.Log.d("ProductCatalog", "=== FAB CLICKED ===");
                    Toast.makeText(getContext(), "Abriendo formulario...", Toast.LENGTH_SHORT).show();
                    handleFabClick();
                });
                fabAddProduct.bringToFront();
                android.util.Log.d("ProductCatalog", "FAB configurado");
            }
        } else {
            android.util.Log.d("ProductCatalog", "Usuario NO es Empresa/Admin - Ocultando botones");
            if (fabAddProduct != null) {
                fabAddProduct.setVisibility(View.GONE);
            }
            if (btnAddProductToolbar != null) {
                btnAddProductToolbar.setVisibility(View.GONE);
            }
        }
        
        android.util.Log.d("ProductCatalog", "=== FIN CONFIGURACIÓN FAB ===");
        
        // Cargar productos y perfiles desde el backend
        loadPerfilesYProductos();
    }
    
    private void handleFabClick() {
        android.util.Log.d("ProductCatalog", "handleFabClick() llamado");
        
        try {
            android.util.Log.d("ProductCatalog", "Intentando obtener NavController...");
            View navView = requireView();
            android.util.Log.d("ProductCatalog", "View para NavController: " + (navView != null));
            
            androidx.navigation.NavController navController = 
                androidx.navigation.Navigation.findNavController(navView);
            android.util.Log.d("ProductCatalog", "NavController obtenido: " + (navController != null));
            
            if (navController != null) {
                android.util.Log.d("ProductCatalog", "Current destination: " + 
                    (navController.getCurrentDestination() != null ? 
                        navController.getCurrentDestination().getId() : "null"));
                
                // Verificar que la acción existe
                int actionId = R.id.action_productCatalogFragment_to_addEditProductFragment;
                android.util.Log.d("ProductCatalog", "Action ID: " + actionId);
                
                android.util.Log.d("ProductCatalog", "Intentando navegar con acción...");
                navController.navigate(actionId);
                android.util.Log.d("ProductCatalog", "Navegación con acción ejecutada");
            } else {
                android.util.Log.e("ProductCatalog", "NavController es null!");
                Toast.makeText(getContext(), "Error: NavController no disponible", Toast.LENGTH_SHORT).show();
            }
            
        } catch (IllegalArgumentException e) {
            android.util.Log.e("ProductCatalog", "IllegalArgumentException: " + e.getMessage(), e);
            android.util.Log.d("ProductCatalog", "Intentando navegación directa como fallback...");
            try {
                androidx.navigation.NavController navController = 
                    androidx.navigation.Navigation.findNavController(requireView());
                navController.navigate(R.id.addEditProductFragment);
                android.util.Log.d("ProductCatalog", "Navegación directa exitosa");
            } catch (Exception e2) {
                android.util.Log.e("ProductCatalog", "Error en navegación directa: " + e2.getMessage(), e2);
                Toast.makeText(getContext(), "Error: " + e2.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (IllegalStateException e) {
            android.util.Log.e("ProductCatalog", "IllegalStateException: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error de estado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            android.util.Log.e("ProductCatalog", "Error inesperado: " + e.getMessage(), e);
            android.util.Log.e("ProductCatalog", "Stack trace: ", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadPerfilesYProductos() {
        progressBar.setVisibility(View.VISIBLE);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        
        // Cargar perfiles comerciales primero
        Call<List<PerfilComercial>> perfilesCall = apiService.getPerfiles();
        perfilesCall.enqueue(new Callback<List<PerfilComercial>>() {
            @Override
            public void onResponse(Call<List<PerfilComercial>> call, Response<List<PerfilComercial>> response) {
                Map<String, String> perfilesMap = new HashMap<>();
                
                if (response.isSuccessful() && response.body() != null) {
                    for (PerfilComercial perfil : response.body()) {
                        if (perfil.getId() != null && perfil.getNombre() != null) {
                            perfilesMap.put(perfil.getId(), perfil.getNombre());
                        }
                    }
                }
                
                // Actualizar el adapter con el mapa de perfiles
                productAdapter.setPerfilesMap(perfilesMap);
                
                // Ahora cargar productos
                loadProductos();
            }

            @Override
            public void onFailure(Call<List<PerfilComercial>> call, Throwable t) {
                // Continuar cargando productos aunque falle perfiles
                loadProductos();
            }
        });
    }

    private void loadProductos() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Producto>> call = apiService.getProductosDisponibles();
        
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    productos = response.body();
                    productAdapter.updateList(productos);
                } else {
                    Toast.makeText(getContext(), "Error al cargar productos: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar productos cuando el fragment vuelve a ser visible
        loadProductos();
    }
}

