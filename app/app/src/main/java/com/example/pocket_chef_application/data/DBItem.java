package com.example.pocket_chef_application.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBItem {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "item_id")
    public String item_id;

    @ColumnInfo(name = "item_name")
    public String item_Name;

    @ColumnInfo(name = "exp_date")
    public String exp_date;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "image_url")
    public String image_url;
}
