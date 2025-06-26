// app/src/main/java/com/example/mogok/ui/history/TransactionDetailAdapter.java
package com.example.mogok.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mogok.R;
import com.example.mogok.models.TransactionDetail;
import com.example.mogok.utils.Helpers;

import java.util.List;

public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.DetailViewHolder> {

    private Context context;
    private List<TransactionDetail> detailList;

    public TransactionDetailAdapter(Context context, List<TransactionDetail> detailList) {
        this.context = context;
        this.detailList = detailList;
    }

    public void updateData(List<TransactionDetail> newData) {
        this.detailList.clear();
        this.detailList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        TransactionDetail detail = detailList.get(position);
        holder.tvPartName.setText(detail.part.name);
        holder.tvQuantity.setText(String.valueOf(detail.quantity));
        holder.tvPriceAtTransaction.setText(Helpers.formatCurrency(detail.priceAtTransaction));
        holder.tvSubtotal.setText(Helpers.formatCurrency(detail.quantity * detail.priceAtTransaction));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartName, tvQuantity, tvPriceAtTransaction, tvSubtotal;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartName = itemView.findViewById(R.id.tvDetailPartName);
            tvQuantity = itemView.findViewById(R.id.tvDetailQuantity);
            tvPriceAtTransaction = itemView.findViewById(R.id.tvDetailPriceAtTransaction);
            tvSubtotal = itemView.findViewById(R.id.tvDetailSubtotal);
        }
    }
}