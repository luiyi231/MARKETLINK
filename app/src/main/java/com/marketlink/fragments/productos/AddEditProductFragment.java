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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.marketlink.R;
import com.marketlink.models.Producto;
import com.marketlink.models.PerfilComercial;
import com.marketlink.models.Categoria;
import com.marketlink.models.Subcategoria;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditProductFragment extends Fragment {

    private TextInputEditText etNombre;
    private TextInputEditText etDescripcion;
    private TextInputEditText etPrecio;
    private TextInputEditText etStock;
    private TextInputEditText etImagenUrl;
    private MaterialAutoCompleteTextView actvCategoria;
    private MaterialAutoCompleteTextView actvSubcategoria;
    private MaterialCheckBox cbDisponible;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;
    private String productoId;
    private String perfilComercialId;
    private String categoriaId;
    private String subcategoriaId;
    private List<PerfilComercial> perfiles;
    private List<Categoria> categorias = new ArrayList<>();
    private List<Subcategoria> subcategorias = new ArrayList<>();
    private android.widget.ArrayAdapter<String> categoriaAdapter;
    private android.widget.ArrayAdapter<String> subcategoriaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(
                    getArguments() != null && getArguments().containsKey("producto_id") 
                        ? "Editar Producto" : "Agregar Producto");
            }
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        }

        // Obtener ID del producto si es edición
        if (getArguments() != null) {
            productoId = getArguments().getString("producto_id");
        }

        etNombre = view.findViewById(R.id.et_nombre);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        etPrecio = view.findViewById(R.id.et_precio);
        etStock = view.findViewById(R.id.et_stock);
        etImagenUrl = view.findViewById(R.id.et_imagen_url);
        actvCategoria = view.findViewById(R.id.actv_categoria);
        actvSubcategoria = view.findViewById(R.id.actv_subcategoria);
        cbDisponible = view.findViewById(R.id.cb_disponible);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        progressBar = view.findViewById(R.id.progress_bar);

        // Cargar categorías y perfiles comerciales
        loadCategorias();
        loadPerfilesComerciales();
        
        // Configurar listener para categoría
        actvCategoria.setOnItemClickListener((parent, view1, position, id) -> {
            String categoriaNombre = (String) parent.getItemAtPosition(position);
            Categoria categoriaSeleccionada = null;
            for (Categoria c : categorias) {
                if (c.getNombre().equals(categoriaNombre)) {
                    categoriaSeleccionada = c;
                    break;
                }
            }
            if (categoriaSeleccionada != null) {
                categoriaId = categoriaSeleccionada.getId();
                actvSubcategoria.setEnabled(true);
                actvSubcategoria.setText("", false);
                subcategoriaId = null;
                loadSubcategorias(categoriaId);
            }
        });
        
        // Configurar listener para subcategoría (se actualizará cuando se carguen las subcategorías)
        actvSubcategoria.setOnItemClickListener((parent, view1, position, id) -> {
            String subcategoriaNombre = (String) parent.getItemAtPosition(position);
            if (!"Ninguna".equals(subcategoriaNombre)) {
                for (Subcategoria sub : subcategorias) {
                    if (sub.getNombre().equals(subcategoriaNombre)) {
                        subcategoriaId = sub.getId();
                        break;
                    }
                }
            } else {
                subcategoriaId = null;
            }
        });

        // Si es edición, cargar datos del producto
        if (productoId != null && !productoId.isEmpty()) {
            loadProducto();
        }

        btnGuardar.setOnClickListener(v -> {
            if (validarFormulario()) {
                guardarProducto();
            }
        });
    }

    private void loadCategorias() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Categoria>> call = apiService.getCategorias();
        
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias = response.body();
                    List<String> nombresCategorias = new ArrayList<>();
                    for (Categoria cat : categorias) {
                        nombresCategorias.add(cat.getNombre());
                    }
                    categoriaAdapter = new android.widget.ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        nombresCategorias
                    );
                    actvCategoria.setAdapter(categoriaAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                android.util.Log.e("AddEditProduct", "Error al cargar categorías: " + t.getMessage());
            }
        });
    }

    private void loadSubcategorias(String categoriaId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Subcategoria>> call = apiService.getSubcategorias(categoriaId);
        
        call.enqueue(new Callback<List<Subcategoria>>() {
            @Override
            public void onResponse(Call<List<Subcategoria>> call, Response<List<Subcategoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subcategorias = response.body();
                    List<String> nombresSubcategorias = new ArrayList<>();
                    nombresSubcategorias.add("Ninguna"); // Opción para no seleccionar subcategoría
                    for (Subcategoria sub : subcategorias) {
                        nombresSubcategorias.add(sub.getNombre());
                    }
                    subcategoriaAdapter = new android.widget.ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        nombresSubcategorias
                    );
                    actvSubcategoria.setAdapter(subcategoriaAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Subcategoria>> call, Throwable t) {
                android.util.Log.e("AddEditProduct", "Error al cargar subcategorías: " + t.getMessage());
            }
        });
    }

    private void loadPerfilesComerciales() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Debe iniciar sesión", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<PerfilComercial>> call = apiService.getMisPerfiles();
        
        call.enqueue(new Callback<List<PerfilComercial>>() {
            @Override
            public void onResponse(Call<List<PerfilComercial>> call, Response<List<PerfilComercial>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    perfiles = response.body();
                    // Usar el primer perfil por defecto
                    if (perfiles.size() > 0) {
                        perfilComercialId = perfiles.get(0).getId();
                    }
                } else {
                    Toast.makeText(getContext(), "Debe tener al menos un perfil comercial", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
            }

            @Override
            public void onFailure(Call<List<PerfilComercial>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar perfiles: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducto() {
        if (productoId == null || productoId.isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Producto> call = apiService.getProducto(productoId);
        
        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    Producto producto = response.body();
                    etNombre.setText(producto.getNombre());
                    etDescripcion.setText(producto.getDescripcion());
                    if (producto.getPrecio() != null) {
                        etPrecio.setText(String.valueOf(producto.getPrecio()));
                    }
                    if (producto.getStock() != null) {
                        etStock.setText(String.valueOf(producto.getStock()));
                    }
                    etImagenUrl.setText(producto.getImagenUrl());
                    perfilComercialId = producto.getPerfilComercialId();
                    categoriaId = producto.getCategoriaId();
                    subcategoriaId = producto.getSubcategoriaId();
                    if (producto.getDisponible() != null) {
                        cbDisponible.setChecked(producto.getDisponible());
                    }
                    
                    // Seleccionar categoría y subcategoría si existen
                    if (categoriaId != null) {
                        Categoria cat = categorias.stream()
                            .filter(c -> c.getId().equals(categoriaId))
                            .findFirst()
                            .orElse(null);
                        if (cat != null) {
                            actvCategoria.setText(cat.getNombre(), false);
                            loadSubcategorias(categoriaId);
                            if (subcategoriaId != null) {
                                Subcategoria sub = subcategorias.stream()
                                    .filter(s -> s.getId().equals(subcategoriaId))
                                    .findFirst()
                                    .orElse(null);
                                if (sub != null) {
                                    actvSubcategoria.setText(sub.getNombre(), false);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar producto: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarFormulario() {
        String nombre = etNombre.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String stock = etStock.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese el nombre del producto");
            etNombre.requestFocus();
            return false;
        }

        if (precio.isEmpty()) {
            etPrecio.setError("Ingrese el precio");
            etPrecio.requestFocus();
            return false;
        }

        try {
            double precioValue = Double.parseDouble(precio);
            if (precioValue <= 0) {
                etPrecio.setError("El precio debe ser mayor a 0");
                etPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etPrecio.setError("Precio inválido");
            etPrecio.requestFocus();
            return false;
        }

        if (stock.isEmpty()) {
            etStock.setError("Ingrese el stock");
            etStock.requestFocus();
            return false;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            if (stockValue < 0) {
                etStock.setError("El stock no puede ser negativo");
                etStock.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etStock.setError("Stock inválido");
            etStock.requestFocus();
            return false;
        }

        if (perfilComercialId == null || perfilComercialId.isEmpty()) {
            Toast.makeText(getContext(), "Debe seleccionar un perfil comercial", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (categoriaId == null || categoriaId.isEmpty()) {
            Toast.makeText(getContext(), "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
            actvCategoria.requestFocus();
            return false;
        }

        return true;
    }

    private void guardarProducto() {
        progressBar.setVisibility(View.VISIBLE);

        Producto producto = new Producto();
        producto.setNombre(etNombre.getText().toString().trim());
        producto.setDescripcion(etDescripcion.getText().toString().trim());
        producto.setPrecio(Double.parseDouble(etPrecio.getText().toString().trim()));
        producto.setStock(Integer.parseInt(etStock.getText().toString().trim()));
        producto.setImagenUrl(etImagenUrl.getText().toString().trim());
        producto.setPerfilComercialId(perfilComercialId);
        producto.setCategoriaId(categoriaId);
        producto.setSubcategoriaId(subcategoriaId);
        producto.setDisponible(cbDisponible.isChecked());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Producto> call;

        if (productoId != null && !productoId.isEmpty()) {
            // Actualizar producto existente
            call = apiService.updateProducto(productoId, producto);
        } else {
            // Crear nuevo producto
            call = apiService.createProducto(producto);
        }

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), 
                        productoId != null ? "Producto actualizado" : "Producto creado exitosamente", 
                        Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    String errorMessage = "Error al guardar producto";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("message")) {
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
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

