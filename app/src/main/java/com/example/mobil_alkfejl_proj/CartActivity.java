package com.example.mobil_alkfejl_proj;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private static CartActivity instance;
    private int itemCount;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private NotificationHandler mNotification;
    private List<ShoppingItem> cartItems;
    private static final String LOG_TAG = CartActivity.class.getName();

    public CartActivity() {
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

    private int points = 0;
    private int totalPoints = 0;

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
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        cartRecyclerView = findViewById(R.id.activityCartRecyclerView);
        cartItems = CartManager.getInstance().getCartItems();

        cartAdapter = new CartAdapter(this, cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        mNotification = new NotificationHandler(this);

//        for (int i = 0; i < cartItems.size(); i++) {
//            points += cartItems.get(i).getPrice();
//        }
//        points /= 100;
        TextView emptyCartMessage = findViewById(R.id.emptyCartMessage);
        Button payButton = findViewById(R.id.payButton);
        Button returnButton = findViewById(R.id.returnButton);
        Button emptyCartButton = findViewById(R.id.emptyCartButton);
//        mNotification.send("Test notification");
//        int finalPoints = points;


        for (ShoppingItem item : cartItems) {
            points += item.getPrice();
            totalPoints += item.getPrice();
        }
//        points /= 100;

        TextView finalPriceTextView = findViewById(R.id.finalPriceTextView);
        finalPriceTextView.setText("Végösszeg: " + totalPoints + " Ft");


        payButton.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, "Köszönjük a vásárlást!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);

//            TextView pointsInput = findViewById(R.id.accountPoints);

            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .whereEqualTo("email", user.getEmail())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                long currentPoints = document.getLong("points");
                                long newPoints = currentPoints + points / 100;
                                db.collection("Users").document(document.getId())
                                        .update("points", newPoints)
                                        .addOnSuccessListener(success -> Log.d(LOG_TAG, "Pontok frissitve"))
                                        .addOnFailureListener(failure -> Log.e(LOG_TAG, "Pontok nem frissultek", failure));
                            }
                        });
            }
            CartManager.getInstance().clearCart();
            CartActivity.getInstance().clearCart();

            mNotification.send("Jóváírtunk számodra " + points / 100 + " pontot!");

        });


        returnButton.setOnClickListener(v -> {
//                Toast.makeText(CartActivity.this, "Köszönjük a vásárlást!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CategoryActivity.class);
            overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
            startActivity(intent);
//                CartManager.getInstance().clearCart();
        });

        emptyCartButton.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, "Kosár ürítve!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
            CartManager.getInstance().clearCart();
            CartActivity.getInstance().clearCart();
        });
        if (!cartItems.isEmpty()) {


            emptyCartMessage.setVisibility(GONE);
            cartRecyclerView.setVisibility(VISIBLE);
            payButton.setVisibility(VISIBLE);
            finalPriceTextView.setVisibility(VISIBLE);
//            returnButton.setVisibility(VISIBLE);
            emptyCartButton.setVisibility(VISIBLE);
        } else {
            emptyCartMessage.setVisibility(VISIBLE);
            cartRecyclerView.setVisibility(GONE);
            payButton.setVisibility(GONE);
            finalPriceTextView.setVisibility(GONE);
//            returnButton.setVisibility(GONE);
            emptyCartButton.setVisibility(GONE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView finalPriceTextView = findViewById(R.id.finalPriceTextView);
        finalPriceTextView.setText("Végösszeg: " + points + " Ft");
    }
}