// app/src/main/java/com/example/mogok/ui/main/MainActivity.java
package com.example.mogok.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mogok.R;
import com.example.mogok.api.RetrofitClient; // Pastikan ini diimport
import com.example.mogok.ui.auth.LoginActivity;
import com.example.mogok.ui.history.TransactionHistoryActivity;
import com.example.mogok.ui.transaction.CheckoutActivity;
import com.example.mogok.utils.AuthManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcomeUser;
    private Button btnLogout, btnCheckout, btnHistory;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        authManager = new AuthManager(this);

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        btnLogout = findViewById(R.id.btnLogout);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnHistory = findViewById(R.id.btnHistory);

        String userName = authManager.getUserName();
        tvWelcomeUser.setText("Welcome, " + userName + "!");

        btnLogout.setOnClickListener(v -> logoutUser());
        btnCheckout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CheckoutActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransactionHistoryActivity.class)));
    }

    private void logoutUser() {
        // Karena AuthInterceptor akan menambahkan token secara otomatis,
        // Anda tidak perlu lagi menambahkan "Bearer " + authManager.getAuthToken() di sini.
        // Cukup panggil method logoutUser() tanpa argumen token jika method di ApiConfig diubah.
        // Jika method logoutUser di ApiConfig masih memerlukan token, maka tetap kirimkan.
        // Mari kita asumsikan logoutUser di ApiConfig tidak lagi menerima token secara manual
        // karena AuthInterceptor akan menanganinya.
        // Contoh: Call<ResponseBody> call = RetrofitClient.getApiConfig().logoutUser();
        // Atau jika harus spesifik untuk logout:
        String token = "Bearer " + authManager.getAuthToken(); // Ini masih mungkin diperlukan untuk rute logout

        // Sesuaikan panggilan ini tergantung pada apakah ApiConfig.logoutUser masih menerima argumen token atau tidak.
        // Jika Anda menghapus @Header("Authorization") dari ApiConfig.logoutUser, maka panggilannya jadi:
        // RetrofitClient.getApiConfig().logoutUser().enqueue(new Callback<ResponseBody>() { ... });
        // Namun, jika logout adalah rute khusus yang memerlukan token di body atau query, atau jika
        // Anda tidak menghapus @Header("Authorization") dari method logoutUser di ApiConfig,
        // maka kode Anda saat ini (`logoutUser(token)`) sudah benar.

        RetrofitClient.getApiConfig().logoutUser(token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    authManager.logout();
                    Toast.makeText(MainActivity.this, "Logout berhasil!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Penanganan error jika logout gagal di server
                    Toast.makeText(MainActivity.this, "Logout gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error jaringan saat logout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}