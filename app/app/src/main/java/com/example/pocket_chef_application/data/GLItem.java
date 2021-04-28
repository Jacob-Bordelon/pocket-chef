package com.example.pocket_chef_application.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Update;



@Entity(tableName = "GroceryItems")
public class GLItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    public long item_id;

    @ColumnInfo(name = "item_name")
    public String item_Name;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "image_url")
    public String image_url;

    public GLItem(String item_Name, int amount) {
        this.item_Name = item_Name;
        this.amount = amount;
    }

    public GLItem() {
    }

    public GLItem(String item_Name, int amount, String image_url) {
        this.item_Name = item_Name;
        this.amount = amount;
        this.image_url = image_url;
    }
}


