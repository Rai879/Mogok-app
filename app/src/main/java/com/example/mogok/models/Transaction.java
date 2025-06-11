package com.example.mogok.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Transaction {
    public int id;
    @SerializedName("invoice_number")
    public String invoiceNumber;
    @SerializedName("total_amount")
    public double totalAmount;
    @SerializedName("cash_paid")
    public double cashPaid;
    @SerializedName("change_due")
    public double changeDue;
    @SerializedName("created_at")
    public String createdAt; // Or use Date object
    public List<TransactionDetail> details; // Nested TransactionDetail objects
}