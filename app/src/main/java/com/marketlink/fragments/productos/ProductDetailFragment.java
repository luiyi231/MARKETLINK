package com.marketlink.fragments.productos;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.marketlink.R;
import com.marketlink.models.Categoria;
import com.marketlink.models.PerfilComercial;
import com.marketlink.models.Producto;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;
import com.marketlink.utils.CartManager;

import java.text.NumberFormat;
import java.util.Locale;

import android.widget.ImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Producto producto;
    private PerfilComercial perfilComercial;
    private Categoria categoria;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    // Views
    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDescription;
    private TextView tvCategory;
    private TextView tvStock;
    private TextView tvCompanyName;
    private TextView tvCompanyAddress;
    private TextView tvCompanyPhone;
    private TextView tvCompanySchedule;
    private MaterialButton btnAddToCart;
    private TextView tvMapMessage;
    private String tipoUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
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
            }
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        }

        // Initialize views
        ivProductImage = view.findViewById(R.id.iv_product_image);
        tvProductName = view.findViewById(R.id.tv_product_name);
        tvProductPrice = view.findViewById(R.id.tv_product_price);
        tvProductDescription = view.findViewById(R.id.tv_product_description);
        tvCategory = view.findViewById(R.id.tv_category);
        tvStock = view.findViewById(R.id.tv_stock);
        tvCompanyName = view.findViewById(R.id.tv_company_name);
        tvCompanyAddress = view.findViewById(R.id.tv_company_address);
        tvCompanyPhone = view.findViewById(R.id.tv_company_phone);
        tvCompanySchedule = view.findViewById(R.id.tv_company_schedule);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        tvMapMessage = view.findViewById(R.id.tv_map_message);

        // Get user type
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        tipoUsuario = prefs.getString("user_tipo", "Cliente");

        // Get product ID from arguments
        String productId = null;
        if (getArguments() != null) {
            productId = getArguments().getString("producto_id");
        }

        if (productId == null || productId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Producto no encontrado", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }

        // Setup map - wait for view to be fully created
        view.post(() -> {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.map_container, mapFragment)
                        .commit();
            }
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Load product data
        loadProducto(productId);

        // Setup add to cart button
        if ("Cliente".equals(tipoUsuario)) {
            btnAddToCart.setVisibility(View.VISIBLE);
            btnAddToCart.setOnClickListener(v -> {
                if (producto != null) {
                    CartManager.getInstance().addItem(producto, 1);
                    Toast.makeText(getContext(), "Agregado al carrito: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                    
                    // Update badge in MainActivity
                    if (getActivity() instanceof com.marketlink.MainActivity) {
                        ((com.marketlink.MainActivity) getActivity()).actualizarBadgeCarrito();
                    }
                }
            });
        } else {
            btnAddToCart.setVisibility(View.GONE);
        }
    }

    private void loadProducto(String productId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Producto> call = apiService.getProducto(productId);

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    producto = response.body();
                    displayProductInfo();
                    
                    // Load category
                    if (producto.getCategoriaId() != null) {
                        loadCategoria(producto.getCategoriaId());
                    }
                    
                    // Load company profile
                    if (producto.getPerfilComercialId() != null) {
                        loadPerfilComercial(producto.getPerfilComercialId());
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar producto: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategoria(String categoriaId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Categoria> call = apiService.getCategoria(categoriaId);

        call.enqueue(new Callback<Categoria>() {
            @Override
            public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoria = response.body();
                    tvCategory.setText(categoria.getNombre());
                }
            }

            @Override
            public void onFailure(Call<Categoria> call, Throwable t) {
                // Silently fail, category is optional
            }
        });
    }

    private void loadPerfilComercial(String perfilId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PerfilComercial> call = apiService.getPerfil(perfilId);

        call.enqueue(new Callback<PerfilComercial>() {
            @Override
            public void onResponse(Call<PerfilComercial> call, Response<PerfilComercial> response) {
                if (response.isSuccessful() && response.body() != null) {
                    perfilComercial = response.body();
                    displayCompanyInfo();
                    updateMap();
                }
            }

            @Override
            public void onFailure(Call<PerfilComercial> call, Throwable t) {
                // Silently fail
            }
        });
    }

    private void displayProductInfo() {
        if (producto == null) return;

        tvProductName.setText(producto.getNombre() != null ? producto.getNombre() : "Sin nombre");
        
        if (producto.getPrecio() != null) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "BO"));
            tvProductPrice.setText(formatter.format(producto.getPrecio()));
        } else {
            tvProductPrice.setText("Bs. 0.00");
        }

        tvProductDescription.setText(producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripción");
        
        if (producto.getStock() != null) {
            tvStock.setText(String.valueOf(producto.getStock()));
        } else {
            tvStock.setText("0");
        }

        // Load product image
        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            Glide.with(this)
                    .load(producto.getImagenUrl())
                    .placeholder(R.drawable.ic_package_2)
                    .error(R.drawable.ic_package_2)
                    .into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.ic_package_2);
        }
    }

    private void displayCompanyInfo() {
        if (perfilComercial == null) return;

        tvCompanyName.setText(perfilComercial.getNombre() != null ? perfilComercial.getNombre() : "Sin nombre");
        tvCompanyAddress.setText(perfilComercial.getDireccion() != null ? perfilComercial.getDireccion() : "Sin dirección");
        tvCompanyPhone.setText(perfilComercial.getTelefono() != null ? perfilComercial.getTelefono() : "Sin teléfono");
        
        String horario = "";
        if (perfilComercial.getHorarioApertura() != null && perfilComercial.getHorarioCierre() != null) {
            horario = perfilComercial.getHorarioApertura() + " - " + perfilComercial.getHorarioCierre();
        } else {
            horario = "No disponible";
        }
        tvCompanySchedule.setText(horario);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // Request location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        
        updateMap();
    }

    private void updateMap() {
        if (googleMap == null || perfilComercial == null) {
            if (tvMapMessage != null) {
                tvMapMessage.setVisibility(View.VISIBLE);
                if (perfilComercial == null) {
                    tvMapMessage.setText("Cargando información de ubicación...");
                }
            }
            return;
        }

        if (perfilComercial.getLatitud() != null && perfilComercial.getLongitud() != null) {
            LatLng companyLocation = new LatLng(
                    perfilComercial.getLatitud(),
                    perfilComercial.getLongitud()
            );

            googleMap.addMarker(new MarkerOptions()
                    .position(companyLocation)
                    .title(perfilComercial.getNombre() != null ? perfilComercial.getNombre() : "Empresa"));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(companyLocation, 15f));
            
            if (tvMapMessage != null) {
                tvMapMessage.setVisibility(View.GONE);
            }
        } else {
            if (tvMapMessage != null) {
                tvMapMessage.setVisibility(View.VISIBLE);
                tvMapMessage.setText("Ubicación no disponible");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (googleMap != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        }
    }
}

