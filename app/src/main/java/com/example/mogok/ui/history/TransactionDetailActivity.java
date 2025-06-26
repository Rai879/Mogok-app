// app/src/main/java/com/example/mogok/ui/history/TransactionDetailActivity.java
package com.example.mogok.ui.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mogok.R;
import com.example.mogok.api.ApiConfig;
import com.example.mogok.api.RetrofitClient;
import com.example.mogok.models.Transaction;
import com.example.mogok.utils.AuthManager;
import com.example.mogok.utils.Helpers;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView tvInvoiceNumber, tvTotalAmount, tvCashPaid, tvChangeDue, tvTransactionDate;
    private RecyclerView rvTransactionDetails;
    private ProgressBar progressBar;
    private TransactionDetailAdapter detailAdapter;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        authManager = new AuthManager(this);

        tvInvoiceNumber = findViewById(R.id.tvDetailInvoiceNumber);
        tvTotalAmount = findViewById(R.id.tvDetailTotalAmount);
        tvCashPaid = findViewById(R.id.tvDetailCashPaid);
        tvChangeDue = findViewById(R.id.tvDetailChangeDue);
        tvTransactionDate = findViewById(R.id.tvDetailDate);
        rvTransactionDetails = findViewById(R.id.rvTransactionDetails);
        progressBar = findViewById(R.id.progressBarDetail);

        rvTransactionDetails.setLayoutManager(new LinearLayoutManager(this));
        detailAdapter = new TransactionDetailAdapter(this, new ArrayList<>()); // Start with empty list
        rvTransactionDetails.setAdapter(detailAdapter);

        int transactionId = getIntent().getIntExtra("transaction_id", -1);
        if (transactionId != -1) {
            fetchTransactionDetails(transactionId);
        } else {
            Toast.makeText(this, "Transaction ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getAuthToken() {
        return "Bearer " + authManager.getAuthToken();
    }

    private void fetchTransactionDetails(int transactionId) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiConfig().getTransactionDetails(getAuthToken(), transactionId)
                .enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            Transaction transaction = response.body();
                            displayTransactionDetails(transaction);
                        } else if (response.code() == 404) {
                            Toast.makeText(TransactionDetailActivity.this, "Transaction not found.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(TransactionDetailActivity.this, "Failed to load details: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(TransactionDetailActivity.this, "Failed to load details.", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(TransactionDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void displayTransactionDetails(Transaction transaction) {
        tvInvoiceNumber.setText("Invoice: " + transaction.invoiceNumber);
        tvTotalAmount.setText(Helpers.formatCurrency(transaction.totalAmount));
        tvCashPaid.setText(Helpers.formatCurrency(transaction.cashPaid));
        tvChangeDue.setText(Helpers.formatCurrency(transaction.changeDue));
        tvTransactionDate.setText("Tanggal: " + transaction.createdAt); // Format this date string if needed

        detailAdapter.updateData(transaction.details);
    }
}