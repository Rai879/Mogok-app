package com.example.mogok.models;

import com.google.gson.annotations.SerializedName;

public class TempTransactionItem {
    public int id;
    @SerializedName("part_id")
    public int partId;
    public int quantity;
    @SerializedName("price_at_transaction")
    public double priceAtTransaction;
    public Part part; // Nested Part object
    // Add created_at, updated_at if needed
}