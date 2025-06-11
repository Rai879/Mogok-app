package com.example.mogok.models;

import com.google.gson.annotations.SerializedName;

public class Part {
    public int id;
    public String name;
    @SerializedName("part_number")
    public String partNumber;
    public double price;
    @SerializedName("stock_quantity")
    public int stockQuantity;
    // Add other part fields if needed
}