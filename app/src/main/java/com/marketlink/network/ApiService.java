package com.marketlink.network;

import com.marketlink.models.AuthResponse;
import com.marketlink.models.LoginRequest;
import com.marketlink.models.RegistroRequest;
import com.marketlink.models.RegistroEmpresaRequest;
import com.marketlink.models.Producto;
import com.marketlink.models.Pedido;
import com.marketlink.models.Categoria;
import com.marketlink.models.Subcategoria;
import com.marketlink.models.Ciudad;
import com.marketlink.models.Plan;
import com.marketlink.models.Empresa;
import com.marketlink.models.PerfilComercial;
import com.marketlink.models.UpdateEstadoRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Auth
    @POST("api/Auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/Auth/registro")
    Call<AuthResponse> registro(@Body RegistroRequest request);

    @POST("api/Auth/registro-empresa")
    Call<AuthResponse> registroEmpresa(@Body RegistroEmpresaRequest request);

    @GET("api/Auth/me")
    Call<AuthResponse> getCurrentUser();

    // Productos
    @GET("api/Productos/disponibles")
    Call<List<Producto>> getProductosDisponibles();

    @GET("api/Productos/{id}")
    Call<Producto> getProducto(@Path("id") String id);

    @GET("api/Productos/categoria/{categoriaId}")
    Call<List<Producto>> getProductosByCategoria(@Path("categoriaId") String categoriaId);

    @GET("api/Productos/perfil/{idPerfil}")
    Call<List<Producto>> getProductosByPerfil(@Path("idPerfil") String idPerfil);

    @POST("api/Productos")
    Call<Producto> createProducto(@Body Producto producto);

    @PUT("api/Productos/{id}")
    Call<Producto> updateProducto(@Path("id") String id, @Body Producto producto);

    @DELETE("api/Productos/{id}")
    Call<Void> deleteProducto(@Path("id") String id);

    // Pedidos
    @POST("api/Pedidos")
    Call<Pedido> createPedido(@Body Pedido pedido);

    @GET("api/Pedidos/{id}")
    Call<Pedido> getPedido(@Path("id") String id);

    @GET("api/Pedidos/mis-pedidos")
    Call<List<Pedido>> getMisPedidos();

    @GET("api/Pedidos/perfil/{idPerfil}")
    Call<List<Pedido>> getPedidosByPerfil(@Path("idPerfil") String idPerfil);

    @PUT("api/Pedidos/{id}/estado")
    Call<Pedido> updatePedidoEstado(@Path("id") String id, @Body UpdateEstadoRequest request);

    // Categorias
    @GET("api/Categorias")
    Call<List<Categoria>> getCategorias();

    @GET("api/Categorias/{id}")
    Call<Categoria> getCategoria(@Path("id") String id);

    // Subcategorias
    @GET("api/Subcategorias")
    Call<List<Subcategoria>> getSubcategorias(@Query("categoriaId") String categoriaId);

    @GET("api/Subcategorias/{id}")
    Call<Subcategoria> getSubcategoria(@Path("id") String id);

    // Ciudades
    @GET("api/Ciudades")
    Call<List<Ciudad>> getCiudades();

    @GET("api/Ciudades/{id}")
    Call<Ciudad> getCiudad(@Path("id") String id);

    // Perfiles Comerciales
    @GET("api/Perfiles")
    Call<List<PerfilComercial>> getPerfiles();

    @GET("api/Perfiles/{id}")
    Call<PerfilComercial> getPerfil(@Path("id") String id);

    @GET("api/Perfiles/mis-perfiles")
    Call<List<PerfilComercial>> getMisPerfiles();

    @POST("api/Perfiles")
    Call<PerfilComercial> createPerfil(@Body PerfilComercial perfil);

    @PUT("api/Perfiles/{id}")
    Call<PerfilComercial> updatePerfil(@Path("id") String id, @Body PerfilComercial perfil);

    // Planes
    @GET("api/Planes")
    Call<List<Plan>> getPlanes();

    @GET("api/Planes/{id}")
    Call<Plan> getPlan(@Path("id") String id);
}
