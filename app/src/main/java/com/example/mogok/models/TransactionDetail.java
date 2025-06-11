package com.example.mogok.models;

import com.google.gson.annotations.SerializedName;

public class TransactionDetail {
    public int id;
    @SerializedName("transaction_id")
    public int transactionId;
    @SerializedName("part_id")
    public int partId;
    public int quantity;
    @SerializedName("price_at_transaction")
    public double priceAtTransaction;
    public Part part; // Nested Part object
}