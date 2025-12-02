package com.marketlink.network;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "https://expandebo-backend.onrender.com/";
    private static Retrofit retrofit = null;
    private static Context appContext = null;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            
            // Add interceptor for JWT token
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    
                    // Get token from SharedPreferences
                    String token = null;
                    if (appContext != null) {
                        SharedPreferences prefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                        token = prefs.getString("auth_token", null);
                    }
                    
                    Request.Builder requestBuilder = original.newBuilder();
                    
                    // Add Authorization header if token exists
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                    
                    requestBuilder.header("Content-Type", "application/json");
                    requestBuilder.header("Accept", "application/json");
                    
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
