package com.marketlink;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.marketlink.network.ApiClient;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    // Getter for drawer layout to be used by fragments
    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved dark mode preference BEFORE super.onCreate() to ensure theme is
        // set correctly
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        
        // Initialize ApiClient
        ApiClient.init(this);
        
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Setup Bottom Navigation with NavController
        if (navController != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Setup Navigation Drawer
        navigationView.setNavigationItemSelectedListener(this);

        // Ensure drawer appears on top
        navigationView.bringToFront();
        navigationView.requestLayout();

        // Hide bottom navigation on login and register screens
        if (navController != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Ocultar navegación en pantallas de autenticación
                if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                    navigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    navigationView.setVisibility(View.VISIBLE);

                    // Configurar menús según el tipo de usuario
                    configurarMenusSegunTipoUsuario();
                }
            });
        }
    }

    /**
     * Configura los menús (bottom navigation y drawer) según el tipo de usuario
     * - Empresa/Administrador: Ver gestión de empresas, usuarios, reportes
     * - Cliente: Ver catálogo de productos, pedidos, tracking, carrito
     */
    private void configurarMenusSegunTipoUsuario() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String tipoUsuario = prefs.getString("user_tipo", "Cliente");

        if (navigationView != null && navigationView.getMenu() != null) {
            MenuItem navCompanies = navigationView.getMenu().findItem(R.id.nav_companies);
            MenuItem navReports = navigationView.getMenu().findItem(R.id.nav_reports);

            if ("Empresa".equals(tipoUsuario) || "Administrador".equals(tipoUsuario)) {
                // Emprendedor puede ver gestión de empresas, usuarios y reportes
                if (navCompanies != null)
                    navCompanies.setVisible(true);
                if (navReports != null)
                    navReports.setVisible(true);
            } else {
                // Cliente NO ve estas opciones administrativas
                if (navCompanies != null)
                    navCompanies.setVisible(false);
                if (navReports != null)
                    navReports.setVisible(false);
            }
        }

        // Configurar bottom navigation según tipo de usuario
        if (bottomNavigationView != null && bottomNavigationView.getMenu() != null) {
            MenuItem homeItem = bottomNavigationView.getMenu().findItem(R.id.homeFragment);
            MenuItem dashboardItem = bottomNavigationView.getMenu().findItem(R.id.dashboardFragment);
            MenuItem cartItem = bottomNavigationView.getMenu().findItem(R.id.cartFragment);
            MenuItem pedidosItem = bottomNavigationView.getMenu().findItem(R.id.pedidosListFragment);
            MenuItem productosItem = bottomNavigationView.getMenu().findItem(R.id.productCatalogFragment);
            
            android.util.Log.d("MainActivity", "Configurando bottom nav - tipoUsuario: " + tipoUsuario);
            
            if ("Cliente".equals(tipoUsuario)) {
                // Cliente ve: Inicio, Pedidos, Carrito, Perfil
                if (homeItem != null) {
                    homeItem.setVisible(true);
                    homeItem.setEnabled(true);
                }
                if (dashboardItem != null) {
                    dashboardItem.setVisible(false);
                    dashboardItem.setEnabled(false);
                }
                if (productosItem != null) {
                    productosItem.setVisible(false);
                    productosItem.setEnabled(false);
                }
                if (cartItem != null) {
                    cartItem.setVisible(true);
                    cartItem.setEnabled(true);
                    // Actualizar badge con cantidad de items
                    actualizarBadgeCarrito(cartItem);
                }
                if (pedidosItem != null) {
                    pedidosItem.setVisible(true);
                }
            } else {
                // Empresa y Administrador ven: Dashboard, Pedidos, Productos, Perfil
                if (homeItem != null) {
                    homeItem.setVisible(false);
                    homeItem.setEnabled(false);
                }
                if (dashboardItem != null) {
                    dashboardItem.setVisible(true);
                    dashboardItem.setEnabled(true);
                }
                if (productosItem != null) {
                    productosItem.setVisible(true);
                    productosItem.setEnabled(true);
                }
                if (cartItem != null) {
                    cartItem.setVisible(false);
                    cartItem.setEnabled(false);
                }
                if (pedidosItem != null) {
                    pedidosItem.setVisible(true);
                }
            }
        }
    }
    
    private void actualizarBadgeCarrito(MenuItem cartItem) {
        try {
            int itemCount = com.marketlink.utils.CartManager.getInstance().getItemCount();
            if (itemCount > 0) {
                // Mostrar badge con cantidad
                BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(cartItem.getItemId());
                badge.setVisible(true);
                badge.setNumber(itemCount);
            } else {
                // Ocultar badge si no hay items
                BadgeDrawable badge = bottomNavigationView.getBadge(cartItem.getItemId());
                if (badge != null) {
                    badge.setVisible(false);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error al actualizar badge: " + e.getMessage());
        }
    }
    
    // Método público para actualizar el badge desde otros fragments
    public void actualizarBadgeCarrito() {
        if (bottomNavigationView != null && bottomNavigationView.getMenu() != null) {
            MenuItem cartItem = bottomNavigationView.getMenu().findItem(R.id.cartFragment);
            if (cartItem != null && cartItem.isVisible()) {
                actualizarBadgeCarrito(cartItem);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // Handle drawer menu items with proper navigation
        if (navController == null) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

        int destinationId = 0;

        if (itemId == R.id.nav_companies) {
            // Navigate to companies fragment
            destinationId = R.id.empresaListFragment;

        } else if (itemId == R.id.nav_reports) {
            // Navigate to reports fragment
            destinationId = R.id.reportesFragment;
        } else if (itemId == R.id.nav_settings) {
            // Navigate to settings fragment
            destinationId = R.id.configuracionFragment;
        } else if (itemId == R.id.nav_help) {
            // Navigate to help fragment
            destinationId = R.id.ayudaFragment;
        } else if (itemId == R.id.nav_about) {
            // Navigate to about fragment
            destinationId = R.id.acercaDeFragment;
        }

        // Navigate to destination if valid
        if (destinationId != 0) {
            try {
                navController.navigate(destinationId);
            } catch (Exception e) {
                // Handle navigation error gracefully
                Toast.makeText(this, "Error de navegación", Toast.LENGTH_SHORT).show();
            }
        }

        // Close drawer after selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}