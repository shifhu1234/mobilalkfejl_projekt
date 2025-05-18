package com.example.mobil_alkfejl_proj;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<ShoppingItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
    public void addItem(ShoppingItem item) {
        cartItems.add(item);
    }

    public List<ShoppingItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getItemCount() {
        return cartItems.size();
    }
}
