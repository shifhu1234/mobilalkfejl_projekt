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

import androidx.activity.EdgeToEdge;
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

public class VegetableProductActivity extends AppCompatActivity implements CartUpdateListener {

    private static final String LOG_TAG = VegetableProductActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private int gridNumber = 1;
    private boolean viewRow = true;
    private int cartItems = 0;
    private TextView vegetableProductText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vegetable_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
//        setContentView(R.layout.activity_fruit_product);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Azonositott felhasznalo FRUITPRODUCT");
        } else {
            Log.d(LOG_TAG, "Nem sikerult bejelentkeztetni a felhasznalot");
            finish();
        }

        mRecyclerView = findViewById(R.id.vegetableRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);


        vegetableProductText = findViewById(R.id.vegetableProductTextView);

        if (user.isAnonymous()) {
            vegetableProductText.setVisibility(VISIBLE);
        } else {
            vegetableProductText.setVisibility(GONE);
        }

//        Log.d(LOG_TAG, "initailizeDataaa");
        initailizeData();
    }

    private void initailizeData() {
        String[] itemList = getResources().getStringArray(R.array.vegetable_product_names);
        String[] itemInfo = getResources().getStringArray(R.array.vegetable_product_description);
        String[] itemPrice = getResources().getStringArray(R.array.vegetable_product_price);

        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.vegetable_product_image);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.vegetable_product_rating);

        mItemList.clear();

        for (int i = 0; i < itemList.length; i++) {
            mItemList.add(new ShoppingItem(
                    itemsImageResource.getResourceId(i, 0),
                    itemsRate.getFloat(i, 0),
                    itemPrice[i],
                    itemInfo[i],
                    itemList[i]
            ));
        }
        itemsImageResource.recycle();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Zöldségek");
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
//        else if (id == R.id.setting_button) {
//            return true;}
        else if (id == R.id.cart) {
            Log.d(LOG_TAG, "CART MEGYNOMVA");
//            Intent intent = new Intent(this, CartActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void updateAlertIcon() {
        CartActivity.getInstance().addItem();
        int cartItems = CartActivity.getInstance().getItemCount();

        if (contentTextView != null) {
            contentTextView.setText(cartItems > 0 ? String.valueOf(cartItems) : "");
        }

        if (redCircle != null) {
            redCircle.setVisibility((cartItems > 0) ? View.VISIBLE : View.GONE);
        }
//        validateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

}