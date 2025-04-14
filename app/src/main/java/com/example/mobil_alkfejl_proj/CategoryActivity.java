package com.example.mobil_alkfejl_proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CategoryActivity extends AppCompatActivity {

    private static final String LOG_TAG = CategoryActivity.class.getName();
    private FirebaseUser user;

    ConstraintLayout fruitCatergory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Azonositott felhasznalo");
        } else {
            Log.d(LOG_TAG, "Nem sikerult bejelentkeztetni a felhasznalot");
            finish();
        }

        fruitCatergory = findViewById(R.id.fruitCategory);
    }

    public void navigateToFruitProducts(View view) {
//        Log.i(LOG_TAG, fruitCatergory.getSceneString());
        Intent intent = new Intent(this, FruitProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }

    public void navigateToVegetableProducts(View view) {
        Intent intent = new Intent(this, VegetableProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }

    public void navigateToHoneyProducts(View view) {
        Intent intent = new Intent(this, HoneyProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }

    public void navigateToPastaProducts(View view) {
        Intent intent = new Intent(this, PastaProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }
}