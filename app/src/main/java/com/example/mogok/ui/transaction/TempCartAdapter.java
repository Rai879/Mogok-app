// app/src/main/java/com/example/mogok/ui/transaction/TempCartAdapter.java
package com.example.mogok.ui.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mogok.R;
import com.example.mogok.models.TempTransactionItem;
import com.example.mogok.utils.Helpers;

import java.util.List;

public class TempCartAdapter extends RecyclerView.Adapter<TempCartAdapter.CartViewHolder> {

    private Context context;
    private List<TempTransactionItem> cartItems;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onQuantityChanged(TempTransactionItem item, int newQuantity);
        void onRemoveItem(TempTransactionItem item);
    }

    public TempCartAdapter(Context context, List<TempTransactionItem> cartItems, OnItemActionListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_temp_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        TempTransactionItem item = cartItems.get(position);
        holder.tvPartName.setText(item.part.name);
        holder.tvPartPrice.setText(Helpers.formatCurrency(item.priceAtTransaction));
        holder.etQuantity.setText(String.valueOf(item.quantity));
        holder.tvSubtotal.setText(Helpers.formatCurrency(item.quantity * item.priceAtTransaction));

        // Update quantity listener
        holder.etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // When focus is lost
                try {
                    int newQuantity = Integer.parseInt(holder.etQuantity.getText().toString());
                    if (newQuantity != item.quantity) {
                        listener.onQuantityChanged(item, newQuantity);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                    holder.etQuantity.setText(String.valueOf(item.quantity)); // Revert to old value
                }
            }
        });

        // Remove item listener
        holder.ivRemove.setOnClickListener(v -> listener.onRemoveItem(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartName, tvPartPrice, tvSubtotal;
        EditText etQuantity;
        ImageView ivRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartName = itemView.findViewById(R.id.tvCartPartName);
            tvPartPrice = itemView.findViewById(R.id.tvCartPartPrice);
            tvSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            etQuantity = itemView.findViewById(R.id.etCartQuantity);
            ivRemove = itemView.findViewById(R.id.ivCartRemove);
        }
    }
}