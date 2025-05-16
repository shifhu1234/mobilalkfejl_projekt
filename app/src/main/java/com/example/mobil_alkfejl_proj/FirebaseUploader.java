package com.example.mobil_alkfejl_proj;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    }

    public void queryData() {

        CollectionReference itemsRef = mFirestore.collection(collectionName);
        mItemList.clear();

        itemsRef.orderBy("name").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShoppingItem item = document.toObject(ShoppingItem.class);
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
                );
                itemsRef.add(item);
            }
        }

        itemImage.recycle();
        itemRate.recycle();
    }
}
