package com.example.mobil_alkfejl_proj;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private static CartActivity instance;
    private int itemCount;

//    private ArrayList<ShoppingItem> itemList;

    private CartActivity() {
        itemCount = 0;
    }

    public static synchronized CartActivity getInstance() {
        if (instance == null) {
            instance = new CartActivity();
        }
        return instance;
    }

    public void addItem() {
        itemCount++;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void clearCart() {
        itemCount = 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}