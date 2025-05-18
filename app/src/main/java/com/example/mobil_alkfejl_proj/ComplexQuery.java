package com.example.mobil_alkfejl_proj;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ComplexQuery {

    public interface QueryCallback {
        void onQueryResult(ArrayList<ShoppingItem> itemList);
    }

    private static final String LOG_TAG = ComplexQuery.class.getName();

    public static void topRated(QueryCallback callback, String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .whereGreaterThan("ratedInfo", 3.5)
                .orderBy("ratedInfo", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(success -> {
                    ArrayList<ShoppingItem> list = new ArrayList<>();
                    for (DocumentSnapshot doc : success) {
                        list.add(doc.toObject(ShoppingItem.class));
                    }
                    callback.onQueryResult(list);
                }).addOnFailureListener(failure -> Log.e(LOG_TAG, "SIKERTELEN RENDEZES", failure));
    }

    public static void alphabetical(QueryCallback callback, String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .orderBy("name", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(success -> {
                    ArrayList<ShoppingItem> list = new ArrayList<>();
                    for (DocumentSnapshot doc : success) {
                        list.add(doc.toObject(ShoppingItem.class));
                    }
                    callback.onQueryResult(list);
                }).addOnFailureListener(failure -> Log.e(LOG_TAG, "SIKERTELEN RENDEZES", failure));
    }

    public static void lowestPrice(QueryCallback callback, String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .orderBy("price", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(success -> {
                    ArrayList<ShoppingItem> list = new ArrayList<>();
                    for (DocumentSnapshot doc : success) {
                        list.add(doc.toObject(ShoppingItem.class));
                    }
                    callback.onQueryResult(list);
                }).addOnFailureListener(failure -> Log.e(LOG_TAG, "SIKERTELEN RENDEZES", failure));
    }

    public static void highestPrice(QueryCallback callback, String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .orderBy("price", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(success -> {
                    ArrayList<ShoppingItem> list = new ArrayList<>();
                    for (DocumentSnapshot doc : success) {
                        list.add(doc.toObject(ShoppingItem.class));
                    }
                    callback.onQueryResult(list);
                }).addOnFailureListener(failure -> Log.e(LOG_TAG, "SIKERTELEN RENDEZES", failure));
    }

    public static void smartBuys(QueryCallback callback, String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .whereGreaterThan("ratedInfo", 3.75)
                .orderBy("ratedInfo", Query.Direction.DESCENDING)
                .orderBy("price", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<ShoppingItem> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        list.add(doc.toObject(ShoppingItem.class));
                    }
                    callback.onQueryResult(list);
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "SIKERTELEN RENDEZES", e));
    }

}
