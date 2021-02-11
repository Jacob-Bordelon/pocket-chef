package com.example.pocket_chef_application.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int iid;

    @ColumnInfo(name = "item_name")
    public String item_Name;

    @ColumnInfo(name = "exp_date")
    public String exp_date;

    @ColumnInfo(name = "amount")
    public int amount;
}
