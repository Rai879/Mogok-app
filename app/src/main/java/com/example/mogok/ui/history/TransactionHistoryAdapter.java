// app/src/main/java/com/example/mogok/ui/history/TransactionHistoryAdapter.java
package com.example.mogok.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mogok.R;
import com.example.mogok.models.Transaction;
import com.example.mogok.utils.Helpers;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Transaction> transactionList;
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionHistoryAdapter(Context context, List<Transaction> transactionList, OnTransactionClickListener listener) {
        this.context = context;
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvInvoiceNumber.setText("Invoice: " + transaction.invoiceNumber);
        holder.tvTotalAmount.setText(Helpers.formatCurrency(transaction.totalAmount));
        holder.tvTransactionDate.setText("Tanggal: " + transaction.createdAt); // Format if needed

        holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceNumber, tvTotalAmount, tvTransactionDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceNumber = itemView.findViewById(R.id.tvHistoryInvoiceNumber);
            tvTotalAmount = itemView.findViewById(R.id.tvHistoryTotalAmount);
            tvTransactionDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }
}