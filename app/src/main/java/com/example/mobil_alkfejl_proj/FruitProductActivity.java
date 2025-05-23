package com.example.mobil_alkfejl_proj;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;


public class FruitProductActivity extends AppCompatActivity implements CartUpdateListener {

    private static final String LOG_TAG = FruitProductActivity.class.getName();
    private static final String COLLECTION_NAME = "FruitProducts";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private final int gridNumber = 1;
    private boolean viewRow = true;
    private TextView fruitProductText;
    private CollectionReference mItems;
    private FirebaseUploader firebaseUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fruit_product);
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

        mRecyclerView = findViewById(R.id.fruitRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        fruitProductText = findViewById(R.id.fruitProductTextView);

        if (user.isAnonymous()) {
            fruitProductText.setVisibility(VISIBLE);

            LinearLayout refreshLin = findViewById(R.id.refreshLayout);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) refreshLin.getLayoutParams();
            int marginInDp = 32;
            int marginInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginInDp,
                    getResources().getDisplayMetrics()
            );
            params.topMargin = marginInPx;
            refreshLin.setLayoutParams(params);
        } else {
            fruitProductText.setVisibility(GONE);
            LinearLayout refreshLin = findViewById(R.id.refreshLayout);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) refreshLin.getLayoutParams();
            int marginInDp = 0;
            int marginInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginInDp,
                    getResources().getDisplayMetrics()
            );
            params.topMargin = marginInPx;
            refreshLin.setLayoutParams(params);
        }

        firebaseUploader = new FirebaseUploader(
                this,
                mAdapter,
                mItemList,
                COLLECTION_NAME,
                R.array.fruit_product_names,
                R.array.fruit_product_description,
                R.array.fruit_product_price,
                R.array.fruit_product_image,
                R.array.fruit_product_rating
        );

        firebaseUploader.queryData();
        //        initailizeData();

        Spinner querySpinner = findViewById(R.id.querySpinner);
        Button queryButton = findViewById(R.id.queryButton);

        queryButton.setOnClickListener(v -> {
            String selected = querySpinner.getSelectedItem().toString();

            ComplexQuery.QueryCallback callback = itemList -> {
                mItemList.clear();
                mItemList.addAll(itemList);
                mAdapter.notifyDataSetChanged();
            };

            switch (selected) {
                case "Legjobb értékelés":
                    ComplexQuery.topRated(callback, COLLECTION_NAME);
                    break;
                case "ABC sorrendben":
                    ComplexQuery.alphabetical(callback, COLLECTION_NAME);
                    break;
                case "Legalacsonyabb ár":
                    ComplexQuery.lowestPrice(callback, COLLECTION_NAME);
                    break;
                case "Legmagasabb ár":
                    ComplexQuery.highestPrice(callback, COLLECTION_NAME);
                    break;
                case "Legjobb ár-érték":
                    ComplexQuery.smartBuys(callback, COLLECTION_NAME);
                    break;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gyümölcsök");
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

        if (user.isAnonymous()) {
            MenuItem accountButton = menu.findItem(R.id.account);
            accountButton.setVisible(false);
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
        } else if (id == R.id.cart) {
            if (user.isAnonymous()) {
                Toast.makeText(this, "A vásárláshoz jelentkezz be!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.account) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
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
        if (layoutManager != null) {
            layoutManager.setSpanCount(spanCount);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = rootView.findViewById(R.id.view_alert_count_textview);

        int cartItems = 0;
        if (CartActivity.getInstance() != null) {
            cartItems = CartActivity.getInstance().getItemCount();
        }

        if (contentTextView != null) {
            contentTextView.setText(cartItems > 0 ? String.valueOf(cartItems) : "");
        }

        if (redCircle != null) {
            redCircle.setVisibility(cartItems > 0 ? View.VISIBLE : View.GONE);
        }

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
//        firebaseUploader.queryData();
    }

    @Override
    public void deleteItem(ShoppingItem item) {
        firebaseUploader.deleteItem(this, item);
//        firebaseUploader.queryData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (CartActivity.getInstance() != null) {
            int cartItems = CartActivity.getInstance().getItemCount();

            if (contentTextView != null) {
                contentTextView.setText(cartItems > 0 ? String.valueOf(cartItems) : "");
            }

            if (redCircle != null) {
                redCircle.setVisibility(cartItems > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }


    public void returnToCategories(View view) {
        Intent intent = new Intent(this, CategoryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
    }
}