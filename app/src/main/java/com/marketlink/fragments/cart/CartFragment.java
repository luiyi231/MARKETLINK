package com.marketlink.fragments.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.marketlink.R;
import com.marketlink.adapters.CartAdapter;
import com.marketlink.models.CartItem;
import com.marketlink.models.Pedido;
import com.marketlink.models.PedidoItem;
import com.marketlink.network.ApiClient;
import com.marketlink.network.ApiService;
import com.marketlink.utils.CartManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private LinearLayout llEmptyCart;
    private com.google.android.material.card.MaterialCardView cardSummary;
    private TextView tvTotal;
    private MaterialButton btnCheckout;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
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

        rvCartItems = view.findViewById(R.id.rv_cart_items);
        llEmptyCart = view.findViewById(R.id.ll_empty_cart);
        cardSummary = view.findViewById(R.id.card_summary);
        tvTotal = view.findViewById(R.id.tv_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));

        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartItemListener() {
            @Override
            public void onQuantityChanged() {
                updateCart();
            }

            @Override
            public void onItemRemoved() {
                updateCart();
            }
        });

        rvCartItems.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            crearPedido();
        });

        updateCart();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Actualizar carrito cuando el fragment vuelve a ser visible
        updateCart();
        
        // Actualizar badge en MainActivity
        if (getActivity() instanceof com.marketlink.MainActivity) {
            ((com.marketlink.MainActivity) getActivity()).actualizarBadgeCarrito();
        }
    }

    private void updateCart() {
        cartItems = CartManager.getInstance().getCartItems();
        
        if (cartItems.isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            llEmptyCart.setVisibility(View.VISIBLE);
            cardSummary.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            llEmptyCart.setVisibility(View.GONE);
            cardSummary.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
            
            cartAdapter.updateList(cartItems);
            
            // Actualizar total
            double total = CartManager.getInstance().getTotal();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "BO"));
            tvTotal.setText(formatter.format(total));
        }
    }

    private void crearPedido() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Debe iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Agrupar productos por perfil comercial
        java.util.Map<String, List<CartItem>> productosPorPerfil = new java.util.HashMap<>();
        
        for (CartItem item : cartItems) {
            String perfilId = item.getProducto().getPerfilComercialId();
            if (perfilId != null && !perfilId.isEmpty()) {
                productosPorPerfil.computeIfAbsent(perfilId, k -> new ArrayList<>()).add(item);
            }
        }

        // Crear un pedido por cada perfil comercial
        final int[] pedidosCreados = {0}; // Usar array para poder modificar desde clase interna
        final int totalPedidos = productosPorPerfil.size();
        
        for (java.util.Map.Entry<String, List<CartItem>> entry : productosPorPerfil.entrySet()) {
            String perfilId = entry.getKey();
            List<CartItem> items = entry.getValue();
            
            Pedido pedido = new Pedido();
            pedido.setPerfilComercialId(perfilId);
            pedido.setEstado("Pendiente");
            
            // Crear items del pedido
            List<PedidoItem> pedidoItems = new ArrayList<>();
            double subtotal = 0.0;
            
            for (CartItem cartItem : items) {
                PedidoItem pedidoItem = new PedidoItem();
                pedidoItem.setProductoId(cartItem.getProducto().getId());
                pedidoItem.setNombreProducto(cartItem.getProducto().getNombre());
                pedidoItem.setCantidad(cartItem.getCantidad());
                pedidoItem.setPrecioUnitario(cartItem.getProducto().getPrecio());
                pedidoItem.setSubtotal(cartItem.getSubtotal());
                
                pedidoItems.add(pedidoItem);
                subtotal += cartItem.getSubtotal();
            }
            
            pedido.setItems(pedidoItems);
            pedido.setSubtotal(subtotal);
            pedido.setTotal(subtotal);
            
            // Crear pedido en el backend
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Pedido> call = apiService.createPedido(pedido);
            
            call.enqueue(new Callback<Pedido>() {
                @Override
                public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                    if (response.isSuccessful()) {
                        pedidosCreados[0]++;
                        if (pedidosCreados[0] >= totalPedidos) {
                            // Todos los pedidos creados
                            CartManager.getInstance().clear();
                            Toast.makeText(getContext(), "Pedido(s) creado(s) exitosamente", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        }
                    } else {
                        String errorMessage = "Error al crear pedido";
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
                public void onFailure(Call<Pedido> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

