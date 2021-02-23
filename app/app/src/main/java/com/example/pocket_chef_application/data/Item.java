package com.example.pocket_chef_application.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey @ColumnInfo(name = "item_name")
    @NonNull
    public String item_Name;

    @ColumnInfo(name = "exp_date")
    public String exp_date;

    @ColumnInfo(name = "amount")
    public int amount;
}
