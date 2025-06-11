// app/src/main/java/com/example/mogok/ui/main/MainActivity.java
package com.example.mogok; // Pastikan package ini benar

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Anda perlu membuat activity_main.xml juga
        // Logika utama aplikasi Anda setelah login akan ada di sini
    }
}