package com.example.mobil_alkfejl_proj;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PastaProductActivity extends AppCompatActivity implements CartUpdateListener {

    private static final String LOG_TAG = PastaProductActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private final int gridNumber = 1;
    private boolean viewRow = true;
    private final int cartItems = 0;

    private TextView pastaProductText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pasta_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Azonositott felhasznalo FRUITPRODUCT");
        } else {
            Log.d(LOG_TAG, "Nem sikerult bejelentkeztetni a felhasznalot");
            finish();
        }

        mRecyclerView = findViewById(R.id.pastaRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);


        pastaProductText = findViewById(R.id.pastaProductTextView);

        if (user.isAnonymous()) {
            pastaProductText.setVisibility(VISIBLE);
        } else {
            pastaProductText.setVisibility(GONE);
        }

//        Log.d(LOG_TAG, "initailizeDataaa");
//        initailizeData();
        firebaseUploader = new FirebaseUploader(
                this,
                mAdapter,
                mItemList,
                "PastaProducts",
                R.array.pasta_product_names,
                R.array.pasta_product_description,
                R.array.pasta_product_price,
                R.array.pasta_product_image,
                R.array.pasta_product_rating
        );

        firebaseUploader.queryData();

    }

    private FirebaseUploader firebaseUploader;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Szárazáru");
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        if (user != null && user.isAnonymous()) {
            MenuItem logoutButton = menu.findItem(R.id.log_out_button);
            menu.findItem(R.id.log_in_button).setVisible(true);
            logoutButton.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.log_out_button) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
            return true;
        }
//        else if (id == R.id.setting_button) {return true;}
        else if (id == R.id.cart) {
            Log.d(LOG_TAG, "CART MEGYNOMVA");
//            Intent intent = new Intent(this, CartActivity.class);
//            startActivity(intent);
            Toast.makeText(this, "Hamarosan érkező funckió ;)!", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.ic_view_gird, 2);
            } else {
                changeSpanCount(item, R.drawable.ic_view_row, 1);
            }
            return true;
        } else {
            if (id == R.id.log_in_button) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

@Override
public void updateAlertIcon(ShoppingItem item) {
    CartActivity.getInstance().addItem();
    int cartItems = CartActivity.getInstance().getItemCount();

    if (contentTextView != null) {
        contentTextView.setText(cartItems > 0 ? String.valueOf(cartItems) : "");
    }

    if (redCircle != null) {
        redCircle.setVisibility((cartItems > 0) ? View.VISIBLE : View.GONE);
    }

    firebaseUploader.addItem(this, item);
//    firebaseUploader.queryData();
}

    @Override
    public void deleteItem(ShoppingItem item) {
        firebaseUploader.deleteItem(this, item);
//        firebaseUploader.queryData();
    }
}
