package com.example.mobil_alkfejl_proj;

import android.content.SharedPreferences;

public class ShoppingItem {
    private String name;
    private String info;
    private int price;
    private float ratedInfo;
    private int imageResource;

    public ShoppingItem(){

    }
    public ShoppingItem(int imageResource, float ratedInfo, int price, String info, String name) {
        this.imageResource = imageResource;
        this.ratedInfo = ratedInfo;
        this.price = price;
        this.info = info;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public int getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }
}
