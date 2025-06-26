package com.example.mogok.api;// package com.example.mogok.api;

import android.content.Context;

import com.example.mogok.api.ApiConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitAuthenticated;
    private static Retrofit retrofitPublic;
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() { // Untuk request yang perlu autentikasi
        if (retrofitAuthenticated == null) {
            if (appContext == null) {
                throw new IllegalStateException("RetrofitClient not initialized. Call initialize(Context) first.");
            }
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new AuthInterceptor(appContext)) // AuthInterceptor di sini
                    .build();
            retrofitAuthenticated = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofitAuthenticated;
    }

    public static Retrofit getClientPublic() { // Untuk request public
        if (retrofitPublic == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient publicClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    // TIDAK ADA AuthInterceptor di sini
                    .build();
            retrofitPublic = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(publicClient)
                    .build();
        }
        return retrofitPublic;
    }

    public static ApiConfig getApiConfig() {
        return getClient().create(ApiConfig.class);
    }

    public static ApiConfig getApiConfigPublic() {
        return getClientPublic().create(ApiConfig.class);
    }
}