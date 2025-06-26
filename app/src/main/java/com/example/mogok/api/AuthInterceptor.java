// package com.example.mogok.api; // Sesuaikan dengan package Anda
package com.example.mogok.api; // Sesuaikan dengan package yang benar

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mogok.utils.AuthManager; // Import AuthManager Anda

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private AuthManager authManager;

    // Anda perlu meneruskan context untuk bisa menginisialisasi AuthManager
    public AuthInterceptor(Context context) {
        this.authManager = new AuthManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = authManager.getAuthToken(); // Ambil token dari AuthManager

        // Jika ada token, tambahkan ke header Authorization
        if (token != null) {
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);
            originalRequest = builder.build();
        }

        return chain.proceed(originalRequest);
    }
}