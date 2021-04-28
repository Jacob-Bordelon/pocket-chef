package com.example.pocket_chef_application.Pantry_utils;

import com.example.pocket_chef_application.data.DBItem;

import java.util.Date;

public class Pantry_Item {
    private DBItem item;
    private String title;
    private Date exp_date;
    private int amount;
    private String imageUrl;

    public Pantry_Item() {
    }

    public Pantry_Item(String title, Date exp_date, int amount, String imageUrl) {
        this.title = title;
        this.exp_date = exp_date;
        this.amount = amount;
        this.imageUrl = imageUrl;
    }

    public Pantry_Item(DBItem item) {
        this.item = item;
        this.title = item.item_Name;
        this.amount = item.amount;
        this.imageUrl = item.image_url;
        this.exp_date = item.exp_date;
    }

    public Pantry_Item(DBItem item, String title, Date exp_date, int amount, String imageUrl) {
        this.item = item;
        this.title = title;
        this.exp_date = exp_date;
        this.amount = amount;
        this.imageUrl = imageUrl;
    }

    public DBItem getItem() {
        return item;
    }

    public void setItem(DBItem item) {
        this.item = item;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getExp_date() {
        return exp_date;
    }

    public void setExp_date(Date exp_date) {
        this.exp_date = exp_date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
