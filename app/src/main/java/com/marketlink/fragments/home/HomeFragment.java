package com.marketlink.fragments.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marketlink.MainActivity;
import com.marketlink.R;
import com.marketlink.adapters.CategoriaAdapter;
import com.marketlink.adapters.PerfilComercialAdapter;
import com.marketlink.models.Categoria;
import com.marketlink.models.Cliente;
import com.marketlink.models.PerfilComercial;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategorias;
    private RecyclerView rvEmpresas;
    private androidx.appcompat.widget.SearchView searchView;
    private ProgressBar progressBar;
    private String ciudadId;
    private List<Categoria> categorias = new ArrayList<>();
    private List<PerfilComercial> perfilesComerciales = new ArrayList<>();
    private List<PerfilComercial> perfilesFiltrados = new ArrayList<>();
    private CategoriaAdapter categoriaAdapter;
    private PerfilComercialAdapter perfilAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();

            if (drawerLayout != null) {
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.homeFragment, R.id.pedidosListFragment, R.id.productCatalogFragment)
                        .setOpenableLayout(drawerLayout).build();

                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        rvCategorias = view.findViewById(R.id.rv_categorias);
        rvEmpresas = view.findViewById(R.id.rv_empresas);
        searchView = view.findViewById(R.id.search_view);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup RecyclerViews
        rvCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvEmpresas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar adaptadores vacíos desde el principio
        categoriaAdapter = new CategoriaAdapter(new ArrayList<>(), categoria -> {
            Bundle args = new Bundle();
            args.putString("categoria_id", categoria.getId());
            Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_productCatalogFragment, args);
        });
        rvCategorias.setAdapter(categoriaAdapter);

        perfilAdapter = new PerfilComercialAdapter(new ArrayList<>(), perfil -> {
            Bundle args = new Bundle();
            args.putString("perfil_id", perfil.getId());
            Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_empresaDetailFragment, args);
        });
        rvEmpresas.setAdapter(perfilAdapter);

        // Cargar categorías primero (no dependen de la ciudad)
        cargarCategorias();

        // Obtener ciudad del cliente y luego cargar empresas
        obtenerCiudadCliente();

        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarEmpresas(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEmpresas(newText);
                return true;
            }
        });
    }

    private void obtenerCiudadCliente() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Debe iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Cliente> call = apiService.getMiPerfilCliente();

        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cliente cliente = response.body();
                    ciudadId = cliente.getCiudadId();
                    android.util.Log.d("HomeFragment", "Ciudad del cliente obtenida: " + ciudadId);
                    cargarEmpresas();
                } else {
                    // 404 significa que el usuario no tiene perfil de cliente aún
                    // Esto es normal para usuarios nuevos, no mostrar error
                    if (response.code() == 404) {
                        android.util.Log.d("HomeFragment", "Usuario no tiene perfil de cliente aún (404)");
                    } else {
                        android.util.Log.e("HomeFragment", "Error al obtener cliente: " + response.code() + " - " + response.message());
                    }
                    // Cargar empresas sin filtro de ciudad
                    ciudadId = null;
                    cargarEmpresas();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                android.util.Log.e("HomeFragment", "Error de conexión al obtener cliente: " + t.getMessage());
                // Cargar empresas sin filtro de ciudad
                ciudadId = null;
                cargarEmpresas();
            }
        });
    }

    private void cargarCategorias() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Categoria>> callCategorias = apiService.getCategorias();
        callCategorias.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias = response.body().stream()
                        .filter(c -> c.getActiva() != null && c.getActiva())
                        .collect(Collectors.toList());
                    
                    // Actualizar adapter con las categorías
                    if (categoriaAdapter != null) {
                        categoriaAdapter.updateList(categorias);
                    } else {
                        categoriaAdapter = new CategoriaAdapter(categorias, categoria -> {
                            Bundle args = new Bundle();
                            args.putString("categoria_id", categoria.getId());
                            Navigation.findNavController(requireView())
                                .navigate(R.id.action_homeFragment_to_productCatalogFragment, args);
                        });
                        rvCategorias.setAdapter(categoriaAdapter);
                    }
                } else {
                    android.util.Log.e("HomeFragment", "Error al cargar categorías: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                android.util.Log.e("HomeFragment", "Error de conexión al cargar categorías: " + t.getMessage());
            }
        });
    }

    private void cargarEmpresas() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        
        // Obtener todos los perfiles comerciales
        Call<List<PerfilComercial>> callPerfiles = apiService.getPerfiles();
        callPerfiles.enqueue(new Callback<List<PerfilComercial>>() {
            @Override
            public void onResponse(Call<List<PerfilComercial>> call, Response<List<PerfilComercial>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("HomeFragment", "Perfiles obtenidos: " + response.body().size());
                    
                    // Filtrar perfiles por ciudad si está disponible
                    if (ciudadId != null && !ciudadId.isEmpty()) {
                        perfilesComerciales = response.body().stream()
                            .filter(p -> p.getActivo() != null && p.getActivo() && 
                                       ciudadId.equals(p.getCiudadId()))
                            .collect(Collectors.toList());
                        android.util.Log.d("HomeFragment", "Perfiles filtrados por ciudad: " + perfilesComerciales.size());
                    } else {
                        // Si no hay ciudad, mostrar todos los perfiles activos
                        perfilesComerciales = response.body().stream()
                            .filter(p -> p.getActivo() != null && p.getActivo())
                            .collect(Collectors.toList());
                        android.util.Log.d("HomeFragment", "Perfiles sin filtro de ciudad: " + perfilesComerciales.size());
                    }
                    
                    perfilesFiltrados = new ArrayList<>(perfilesComerciales);
                    mostrarEmpresas();
                } else {
                    android.util.Log.e("HomeFragment", "Error al cargar perfiles: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Error al cargar empresas: " + response.code(), Toast.LENGTH_SHORT).show();
                    // Mostrar lista vacía
                    perfilesComerciales = new ArrayList<>();
                    perfilesFiltrados = new ArrayList<>();
                    mostrarEmpresas();
                }
            }

            @Override
            public void onFailure(Call<List<PerfilComercial>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                android.util.Log.e("HomeFragment", "Error de conexión al cargar perfiles: " + t.getMessage());
                Toast.makeText(getContext(), "Error al cargar empresas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Mostrar lista vacía
                perfilesComerciales = new ArrayList<>();
                perfilesFiltrados = new ArrayList<>();
                mostrarEmpresas();
            }
        });
    }

    private void mostrarEmpresas() {
        if (perfilAdapter != null) {
            perfilAdapter.updateList(perfilesFiltrados);
        } else {
            perfilAdapter = new PerfilComercialAdapter(perfilesFiltrados, perfil -> {
                Bundle args = new Bundle();
                args.putString("perfil_id", perfil.getId());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_empresaDetailFragment, args);
            });
            rvEmpresas.setAdapter(perfilAdapter);
        }
        android.util.Log.d("HomeFragment", "Empresas mostradas: " + perfilesFiltrados.size());
    }

    private void filtrarEmpresas(String query) {
        if (query == null || query.isEmpty()) {
            perfilesFiltrados = new ArrayList<>(perfilesComerciales);
        } else {
            String queryLower = query.toLowerCase();
            perfilesFiltrados = perfilesComerciales.stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(queryLower)) ||
                           (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(queryLower)))
                .collect(Collectors.toList());
        }
        mostrarEmpresas();
    }
}

