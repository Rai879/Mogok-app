// app/src/main/java/com/example/mogok/ui/history/TransactionHistoryActivity.java
package com.example.mogok.ui.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mogok.R;
import com.example.mogok.api.ApiConfig;
import com.example.mogok.api.RetrofitClient;
import com.example.mogok.models.Transaction;
import com.example.mogok.utils.AuthManager;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity implements TransactionHistoryAdapter.OnTransactionClickListener {

    private RecyclerView rvTransactionHistory;
    private TransactionHistoryAdapter historyAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private AuthManager authManager;

    private int currentPage = 1;
    private int lastPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        authManager = new AuthManager(this);
        rvTransactionHistory = findViewById(R.id.rvTransactionHistory);
        rvTransactionHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new TransactionHistoryAdapter(this, transactionList, this);
        rvTransactionHistory.setAdapter(historyAdapter);

        // Implement pagination scrolling
        rvTransactionHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == transactionList.size() - 1) {
                    // scrolled to the end
                    if (!isLoading && currentPage < lastPage) {
                        currentPage++;
                        fetchTransactionHistory(currentPage);
                    }
                }
            }
        });

        fetchTransactionHistory(currentPage);
    }

    private String getAuthToken() {
        return "Bearer " + authManager.getAuthToken();
    }

    private void fetchTransactionHistory(int page) {
        isLoading = true;
        RetrofitClient.getApiConfig().getTransactionHistory(getAuthToken(), page)
                .enqueue(new Callback<ApiConfig.TransactionHistoryResponse>() {
                    @Override
                    public void onResponse(Call<ApiConfig.TransactionHistoryResponse> call, Response<ApiConfig.TransactionHistoryResponse> response) {
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null) {
                            transactionList.addAll(response.body().data);
                            historyAdapter.notifyDataSetChanged();
                            currentPage = response.body().current_page;
                            lastPage = response.body().last_page;
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(TransactionHistoryActivity.this, "Failed to load history: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(TransactionHistoryActivity.this, "Failed to load history.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiConfig.TransactionHistoryResponse> call, Throwable t) {
                        isLoading = false;
                        Toast.makeText(TransactionHistoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onTransactionClick(Transaction transaction) {
        Intent intent = new Intent(TransactionHistoryActivity.this, TransactionDetailActivity.class);
        intent.putExtra("transaction_id", transaction.id);
        startActivity(intent);
    }
}