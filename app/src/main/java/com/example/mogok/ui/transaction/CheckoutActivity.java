// app/src/main/java/com/example/mogok/ui/transaction/CheckoutActivity.java
package com.example.mogok.ui.transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.example.mogok.R;
import com.example.mogok.api.ApiConfig;
import com.example.mogok.api.RetrofitClient;
import com.example.mogok.models.Part;
import com.example.mogok.models.TempTransactionItem;
import com.example.mogok.utils.AuthManager;
import com.example.mogok.utils.Helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements TempCartAdapter.OnItemActionListener, SearchPartAdapter.OnPartSelectedListener {

    private EditText etBarcode, etPartSearch, etCashPaid;
    private TextView tvTotalAmount, tvChangeDue;
    private Button btnProcessTransaction;
    private RecyclerView rvCartItems, rvSearchResults;

    private TempCartAdapter tempCartAdapter;
    private SearchPartAdapter searchPartAdapter;
    private List<TempTransactionItem> cartItems = new ArrayList<>();
    private List<Part> searchResults = new ArrayList<>();

    private double currentTotalAmount = 0.0;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        authManager = new AuthManager(this);

        // Inisialisasi UI components
        etBarcode = findViewById(R.id.etBarcode);
        etPartSearch = findViewById(R.id.etPartSearch);
        etCashPaid = findViewById(R.id.etCashPaid);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvChangeDue = findViewById(R.id.tvChangeDue);
        btnProcessTransaction = findViewById(R.id.btnProcessTransaction);
        rvCartItems = findViewById(R.id.rvCartItems);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        // Setup RecyclerViews
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        tempCartAdapter = new TempCartAdapter(this, cartItems, this); // 'this' for OnItemActionListener
        rvCartItems.setAdapter(tempCartAdapter);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchPartAdapter = new SearchPartAdapter(this, searchResults, this); // 'this' for OnPartSelectedListener
        rvSearchResults.setAdapter(searchPartAdapter);

        // Listeners for input fields
        etBarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String barcode = etBarcode.getText().toString().trim();
                if (!barcode.isEmpty()) {
                    fetchPartByBarcode(barcode); // Panggil metode ini
                    etBarcode.setText(""); // Clear barcode input after scan/entry
                    hideKeyboard(etBarcode);
                }
                return true;
            }
            return false;
        });

        etPartSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() > 2) { // Start searching after 2 characters
                    searchParts(query); // Panggil metode ini
                    rvSearchResults.setVisibility(View.VISIBLE);
                } else {
                    searchResults.clear();
                    searchPartAdapter.notifyDataSetChanged();
                    rvSearchResults.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etCashPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateChange();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnProcessTransaction.setOnClickListener(v -> processTransaction());

        // Initial data load
        // fetchTempTransactions(); // Biarkan ini tetap terautentikasi
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String getAuthToken() {
        return "Bearer " + authManager.getAuthToken();
    }

    private void fetchTempTransactions() {
        // Metode ini tetap memerlukan autentikasi
        RetrofitClient.getApiConfig().getTempTransactions() // Hapus getAuthToken() jika sudah pakai AuthInterceptor
                .enqueue(new Callback<ApiConfig.TempTransactionsResponse>() {
                    @Override
                    public void onResponse(Call<ApiConfig.TempTransactionsResponse> call, Response<ApiConfig.TempTransactionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            cartItems.clear();
                            cartItems.addAll(response.body().items);
                            tempCartAdapter.notifyDataSetChanged();
                            currentTotalAmount = response.body().total_amount;
                            tvTotalAmount.setText(Helpers.formatCurrency(currentTotalAmount));
                            calculateChange(); // Recalculate change after updating total
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Failed to load cart: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Failed to load cart.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiConfig.TempTransactionsResponse> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- PERUBAHAN DI SINI UNTUK TIDAK MENGGUNAKAN AUTENTIKASI ---
    private void fetchPartByBarcode(String barcode) {
        // Karena rute ini sudah public di Laravel, panggil getApiConfigPublic()
        RetrofitClient.getApiConfigPublic().getPartByBarcode(barcode) // <--- UBAH INI
                .enqueue(new Callback<Part>() {
                    @Override
                    public void onResponse(Call<Part> call, Response<Part> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Part part = response.body();
                            addToCart(part.id, 1); // Add with default quantity 1 (Ini memerlukan auth)
                        } else if (response.code() == 404) {
                            Toast.makeText(CheckoutActivity.this, "Part not found for barcode: " + barcode, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Failed to get part by barcode: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Failed to get part by barcode.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Part> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- PERUBAHAN DI SINI UNTUK TIDAK MENGGUNAKAN AUTENTIKASI ---
    private void searchParts(String query) {
        // Karena rute ini sudah public di Laravel, panggil getApiConfigPublic()
        RetrofitClient.getApiConfigPublic().searchParts(query) // <--- UBAH INI
                .enqueue(new Callback<List<Part>>() {
                    @Override
                    public void onResponse(Call<List<Part>> call, Response<List<Part>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            searchResults.clear();
                            searchResults.addAll(response.body());
                            searchPartAdapter.notifyDataSetChanged();
                            if (searchResults.isEmpty()) {
                                rvSearchResults.setVisibility(View.GONE);
                            } else {
                                rvSearchResults.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Handle search errors
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Search failed: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Search failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Part>> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToCart(int partId, int quantity) {
        Map<String, Object> params = new HashMap<>();
        params.put("part_id", partId);
        params.put("quantity", quantity);

        RetrofitClient.getApiConfig().addToCart(params) // Hapus getAuthToken() jika sudah pakai AuthInterceptor
                .enqueue(new Callback<ApiConfig.AddToCartResponse>() {
                    @Override
                    public void onResponse(Call<ApiConfig.AddToCartResponse> call, Response<ApiConfig.AddToCartResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(CheckoutActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            fetchTempTransactions(); // Refresh cart
                            rvSearchResults.setVisibility(View.GONE); // Hide search results after adding
                            etPartSearch.setText(""); // Clear search input
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Failed to add to cart: " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Failed to add to cart.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiConfig.AddToCartResponse> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onQuantityChanged(TempTransactionItem item, int newQuantity) {
        if (newQuantity < 1) {
            Toast.makeText(this, "Quantity cannot be less than 1.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Integer> params = new HashMap<>();
        params.put("quantity", newQuantity);

        RetrofitClient.getApiConfig().updateCartQuantity(item.id, params) // Hapus getAuthToken() jika sudah pakai AuthInterceptor
                .enqueue(new Callback<TempTransactionItem>() {
                    @Override
                    public void onResponse(Call<TempTransactionItem> call, Response<TempTransactionItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(CheckoutActivity.this, "Quantity updated.", Toast.LENGTH_SHORT).show();
                            fetchTempTransactions(); // Refresh cart to update total
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Failed to update quantity: " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Failed to update quantity.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TempTransactionItem> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRemoveItem(TempTransactionItem item) {
        RetrofitClient.getApiConfig().removeFromCart(item.id) // Hapus getAuthToken() jika sudah pakai AuthInterceptor
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CheckoutActivity.this, "Item removed.", Toast.LENGTH_SHORT).show();
                            fetchTempTransactions(); // Refresh cart
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Failed to remove item: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Failed to remove item.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPartSelected(Part part) {
        addToCart(part.id, 1); // Add selected part to cart with default quantity 1
    }

    private void calculateChange() {
        String cashPaidStr = etCashPaid.getText().toString();
        double cashPaid = cashPaidStr.isEmpty() ? 0.0 : Double.parseDouble(cashPaidStr);
        double change = cashPaid - currentTotalAmount;
        tvChangeDue.setText(Helpers.formatCurrency(change));
    }

    private void processTransaction() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty. Please add items.", Toast.LENGTH_SHORT).show();
            return;
        }

        String cashPaidStr = etCashPaid.getText().toString();
        if (cashPaidStr.isEmpty()) {
            Toast.makeText(this, "Please enter cash paid amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        double cashPaid;
        try {
            cashPaid = Double.parseDouble(cashPaidStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid cash amount entered.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cashPaid < currentTotalAmount) {
            Toast.makeText(this, "Cash paid is less than total amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Double> params = new HashMap<>();
        params.put("cash_paid", cashPaid);

        RetrofitClient.getApiConfig().processTransaction(params) // Hapus getAuthToken() jika sudah pakai AuthInterceptor
                .enqueue(new Callback<ApiConfig.ProcessTransactionResponse>() {
                    @Override
                    public void onResponse(Call<ApiConfig.ProcessTransactionResponse> call, Response<ApiConfig.ProcessTransactionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(CheckoutActivity.this, response.body().message + " Kembalian: " + Helpers.formatCurrency(response.body().change_due), Toast.LENGTH_LONG).show();
                            // Clear cart and reset UI after successful transaction
                            cartItems.clear();
                            tempCartAdapter.notifyDataSetChanged();
                            currentTotalAmount = 0.0;
                            etCashPaid.setText("");
                            tvTotalAmount.setText(Helpers.formatCurrency(0.0));
                            tvChangeDue.setText(Helpers.formatCurrency(0.0));
                            // Optionally navigate to history or print receipt
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(CheckoutActivity.this, "Transaction failed: " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(CheckoutActivity.this, "Transaction failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiConfig.ProcessTransactionResponse> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}