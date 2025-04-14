package com.example.mobil_alkfejl_proj;

public class ShoppingItem {
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private final int imageResource;

    public ShoppingItem(int imageResource, float ratedInfo, String price, String info, String name) {
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

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }
}
