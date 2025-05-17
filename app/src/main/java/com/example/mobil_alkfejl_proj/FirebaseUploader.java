package com.example.mobil_alkfejl_proj;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUploader {

    private static final String LOG_TAG = FirebaseUploader.class.getName();

    private final Context context;
    private final FirebaseFirestore mFirestore;
    private final ShoppingItemAdapter mAdapter;
    private final List<ShoppingItem> mItemList;
    private final String collectionName;
    private final int itemNameArr;
    private final int itemInfoArr;
    private final int itemPriceArr;
    private final int itemImageArr;
    private final int itemRatingArr;

    private final CollectionReference mItems;

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

        itemsRef.orderBy("productCount", Query.Direction.DESCENDING).limit(10).get().
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
                        itemName[i],
                        0
                );
                itemsRef.add(item);
            }
        }

        itemImage.recycle();
        itemRate.recycle();
    }

    public void addItem(Context context, ShoppingItem item) {
//        invalidateOptionsMenu();
        mItems.document(item._getId()).update("productCount", item.getProductCount() + 1).addOnSuccessListener(success -> {
            Toast.makeText(context, item.getName() + " a kosárhoz hozzáadva!", Toast.LENGTH_SHORT).show();
        });
        queryData();
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
}
