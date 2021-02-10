package com.example.pocket_chef_application.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pantry {

    @PrimaryKey( autoGenerate = true)
    public int ingID;

    @ColumnInfo(name = "IName")
    public String IName;

    @ColumnInfo(name = "ICalories")
    public int ICalories;

    @ColumnInfo(name = "IExpDate")
    public String IExpDate;

    @ColumnInfo(name = "IAmount")
    public int IAmount;

}
