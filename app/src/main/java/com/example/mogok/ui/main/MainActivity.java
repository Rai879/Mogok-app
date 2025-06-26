// app/src/main/java/com/example/mogok/ui/main/MainActivity.java
package com.example.mogok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mogok.R;
import com.example.mogok.api.RetrofitClient;
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
        btnCheckout = findViewById(R.id.btnCheckout); // Pastikan ID ini ada di activity_main.xml
        btnHistory = findViewById(R.id.btnHistory); // Pastikan ID ini ada di activity_main.xml

        String userName = authManager.getUserName();
        tvWelcomeUser.setText("Welcome, " + userName + "!");

        btnLogout.setOnClickListener(v -> logoutUser());
        btnCheckout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CheckoutActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransactionHistoryActivity.class)));
    }

    private void logoutUser() {
        String token = "Bearer " + authManager.getAuthToken();
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