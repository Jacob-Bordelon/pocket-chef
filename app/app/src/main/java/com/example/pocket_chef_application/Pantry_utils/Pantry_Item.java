package com.example.pocket_chef_application.Pantry_utils;

import com.example.pocket_chef_application.data.DBItem;

public class Pantry_Item {
    private DBItem item;
    private String title;
    private String exp_date;
    private int amount;
    private String imageUrl;

    public Pantry_Item(DBItem item) {
        this.item = item;
        this.title = item.item_Name;
        this.exp_date = item.exp_date;
        this.amount = item.amount;
        this.imageUrl = item.image_url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DBItem getItem() {
        return item;
    }

    public String getTitle() {
        return title;
    }

    public String getExp_date() {
        return exp_date;
    }

    public int getAmount() {
        return amount;
    }

    public void setItem(DBItem item) {
        this.item = item;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setExp_date(String exp_date) {
        this.exp_date = exp_date;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
