package com.marketlink.fragments.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.marketlink.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etEmail = view.findViewById(R.id.et_email);
        TextInputEditText etPassword = view.findViewById(R.id.et_password);
        MaterialButton btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Real Login with Retrofit
                performLogin(email, password, view);
            }
        });

        // Navegar a la pantalla de registro
        view.findViewById(R.id.tv_register).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }

    private void performLogin(String email, String password, View view) {
        com.marketlink.network.ApiService apiService = com.marketlink.network.ApiClient.getClient()
                .create(com.marketlink.network.ApiService.class);
        com.marketlink.models.LoginRequest loginRequest = new com.marketlink.models.LoginRequest(email, password);

        retrofit2.Call<com.marketlink.models.AuthResponse> call = apiService.login(loginRequest);
        call.enqueue(new retrofit2.Callback<com.marketlink.models.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.marketlink.models.AuthResponse> call,
                    retrofit2.Response<com.marketlink.models.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.marketlink.models.AuthResponse authResponse = response.body();

                    // Guardar token y datos del usuario
                    SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("auth_token", authResponse.getToken());
                    editor.putString("user_id", authResponse.getUsuarioId());
                    editor.putString("user_email", authResponse.getEmail());
                    editor.putString("user_name", authResponse.getNombre());
                    editor.putString("user_tipo", authResponse.getRol());
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    Toast.makeText(getContext(), "Bienvenido " + authResponse.getNombre(), Toast.LENGTH_SHORT).show();

                    // Navigate to Dashboard
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_dashboardFragment);
                } else {
                    Toast.makeText(getContext(), "Login fallido: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.marketlink.models.AuthResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
