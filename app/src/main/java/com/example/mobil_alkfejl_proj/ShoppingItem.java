package com.example.mobil_alkfejl_proj;

public class ShoppingItem {
    private String name;
    private String info;
    private int price;
    private float ratedInfo;
    private int imageResource;
    private String id;

    public ShoppingItem() {

    }

    public ShoppingItem(int imageResource, float ratedInfo, int price, String info, String name) {
        this.imageResource = imageResource;
        this.ratedInfo = ratedInfo;
        this.price = price;
        this.info = info;
        this.name = name;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
