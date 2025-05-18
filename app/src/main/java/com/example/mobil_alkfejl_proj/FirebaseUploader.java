package com.example.mobil_alkfejl_proj;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUploader {

    private static final String LOG_TAG = FirebaseUploader.class.getName();

    private final Context context;
    private final FirebaseFirestore mFirestore;
    private ShoppingItemAdapter mAdapter;
    private List<ShoppingItem> mItemList;
    private String collectionName;
    private int itemNameArr;
    private int itemInfoArr;
    private int itemPriceArr;
    private int itemImageArr;
    private int itemRatingArr;

    private CollectionReference mItems;

    public FirebaseUploader(Context context) {
        this.context = context;
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public FirebaseUploader(Context context, ShoppingItemAdapter mAdapter, List<ShoppingItem> itemList,
                            String collectionName,
                            int itemNameArr, int itemInfoArr, int itemPriceArr,
                            int itemImageArr, int itemRatingArr) {
        this.context = context;
        this.mAdapter = mAdapter;
        this.mItemList = itemList;

        this.collectionName = collectionName;
        this.itemNameArr = itemNameArr;
        this.itemInfoArr = itemInfoArr;
        this.itemPriceArr = itemPriceArr;
        this.itemImageArr = itemImageArr;
        this.itemRatingArr = itemRatingArr;

        this.mFirestore = FirebaseFirestore.getInstance();
        this.mItems = mFirestore.collection(collectionName);
//        queryData();
    }

    public void queryData() {

        CollectionReference itemsRef = mFirestore.collection(collectionName);
        mItemList.clear();

        itemsRef.orderBy("name", Query.Direction.DESCENDING).limit(10).get().
                addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ShoppingItem item = document.toObject(ShoppingItem.class);
                        item.setId(document.getId());
                        mItemList.add(item);
                    }

                    if (mItemList.isEmpty()) {
                        initializeData(itemsRef);
                        queryData();
                    }

                    mAdapter.notifyDataSetChanged();
                });
    }

    private void initializeData(CollectionReference itemsRef) {
        String[] itemName = context.getResources().getStringArray(itemNameArr);
        String[] itemInfo = context.getResources().getStringArray(itemInfoArr);
        String[] itemPrice = context.getResources().getStringArray(itemPriceArr);

        TypedArray itemImage = context.getResources().obtainTypedArray(itemImageArr);
        TypedArray itemRate = context.getResources().obtainTypedArray(itemRatingArr);

        for (int i = 0; i < itemName.length; i++) {
            if (itemPrice[i] != null) {
                int price = Integer.parseInt(itemPrice[i]);
                ShoppingItem item = new ShoppingItem(
                        itemImage.getResourceId(i, 0),
                        itemRate.getFloat(i, 0),
                        price,
                        itemInfo[i],
                        itemName[i]
//                        0
                );
                itemsRef.add(item);
            }
        }

        itemImage.recycle();
        itemRate.recycle();
    }

    public void addItem(Context context, ShoppingItem item) {
//        invalidateOptionsMenu();
//        mItems.document(item._getId()).update("productCount", item.getProductCount() + 1).addOnSuccessListener(success -> {
        Toast.makeText(context, item.getName() + " a kosárhoz hozzáadva!", Toast.LENGTH_SHORT).show();
//        });
        CartManager.getInstance().addItem(item);
        //NE FRISSITSE FOLYAMAT
//        queryData();
//        createTransaction(item);
    }

    public void deleteItem(Context context, ShoppingItem item) {
//        invalidateOptionsMenu();
        DocumentReference ref = mItems.document(item._getId());
        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Elem törölve: " + item._getId());
//            Toast.makeText(context, "Elem törölve! " + item._getId(), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(failure -> {
            Toast.makeText(context, "Sikertelen törlés! " + item._getId(), Toast.LENGTH_SHORT).show();
        });
        queryData();
    }

    public void createUser(String email, String name) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference itemsRef = mFirestore.collection("Users");

        if (currentUser != null) {
            String uid = currentUser.getUid();

            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("name", name);
            userData.put("uid", uid);

            FirebaseFirestore.getInstance().collection("Users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener(success -> {
                        Log.d(LOG_TAG, "MENTVE");
                    })
                    .addOnFailureListener(failure -> {
                        Log.e(LOG_TAG, "HIBA MENTES KOZBEN");
                    });
        } else {
            Log.w(LOG_TAG, "NINCS BEJELENTKEZVE SENKI");
        }
    }

    public void createTransaction(ShoppingItem item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference itemsRef = mFirestore.collection("Transactions");

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference newTransactionRef = mFirestore.collection("Transactions").document();
            String transactionId = newTransactionRef.getId();

            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("id", transactionId);
            transactionData.put("itemName", item.getName());
            transactionData.put("price", item.getPrice());
            transactionData.put("quantity", 1);
            transactionData.put("userId", userId);

            newTransactionRef.set(transactionData)
                    .addOnSuccessListener(success -> {
                        Log.d(LOG_TAG, "TRANZAKCIO HOZZAADVA: " + transactionId);
                    })
                    .addOnFailureListener(failure -> {
                        Log.w(LOG_TAG, "HIBA MENTES KOZBEN");
                    });
        } else {
            Log.w(LOG_TAG, "NINCS BEJELENTKEZVE SENKI");
        }
    }


}
