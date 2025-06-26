// app/src/main/java/com/example/mogok/utils/Helpers.java
package com.example.mogok.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class Helpers {

    public static String formatCurrency(double amount) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatRupiah.format(amount);
    }

    // Tambahkan helper lain jika diperlukan
}