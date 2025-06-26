// app/src/main/java/com/example/mogok/ui/transaction/SearchPartAdapter.java
package com.example.mogok.ui.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mogok.R;
import com.example.mogok.models.Part;
import com.example.mogok.utils.Helpers;

import java.util.List;

public class SearchPartAdapter extends RecyclerView.Adapter<SearchPartAdapter.SearchViewHolder> {

    private Context context;
    private List<Part> searchResults;
    private OnPartSelectedListener listener;

    public interface OnPartSelectedListener {
        void onPartSelected(Part part);
    }

    public SearchPartAdapter(Context context, List<Part> searchResults, OnPartSelectedListener listener) {
        this.context = context;
        this.searchResults = searchResults;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Part part = searchResults.get(position);
        holder.tvPartInfo.setText(String.format("%s (%s) - %s | Stok: %d",
                part.name, part.partNumber, Helpers.formatCurrency(part.price), part.stockQuantity));

        holder.itemView.setOnClickListener(v -> listener.onPartSelected(part));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartInfo;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartInfo = itemView.findViewById(R.id.tvSearchPartInfo);
        }
    }
}