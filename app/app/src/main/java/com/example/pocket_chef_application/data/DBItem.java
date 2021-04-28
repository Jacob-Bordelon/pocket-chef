package com.example.pocket_chef_application.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.pocket_chef_application.Model.Food;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "PantryItems")
@TypeConverters({DateConverter.class})
public class DBItem {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "item_id")
    public String item_id;

    @ColumnInfo(name = "item_name")
    public String item_Name;

    @ColumnInfo(name = "exp_date")
    public Date exp_date;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "image_url")
    public String image_url;

    @ColumnInfo(name = "created_at")
    public Date created_at;


    public DBItem(Food food, int amount, Date exp_date){
        this.item_id = Integer.toString(food.getFdcId());
        this.item_Name = food.getName();
        this.amount = amount;
        this.exp_date = exp_date;
        this.image_url = food.getImage();
        this.total_fat = food.getFats();
        this.calories = food.getCalories();
        this.carbs = food.getCarbs();
        this.cholesterol = food.getChols();
        this.sodium = food.getSodium();
        this.protein = food.getProtein();
        this.created_at = Calendar.getInstance().getTime();
    }

    public DBItem(@org.jetbrains.annotations.NotNull String item_id, String item_Name, int amount, Date exp_date){
        this.item_id = item_id;
        this.item_Name = item_Name;
        this.amount = amount;
        this.exp_date = exp_date;
        this.created_at = Calendar.getInstance().getTime();
    }


    @ColumnInfo(name = "modified_at")
    public Date modified_at;

    @ColumnInfo(name = "calories")
    public int calories;

    @ColumnInfo(name = "total_fat")
    public int total_fat;

    @ColumnInfo(name = "cholesterol")
    public int cholesterol;

    @ColumnInfo(name = "sodium")
    public int sodium;

    @ColumnInfo(name = "carbs")
    public int carbs;

    @ColumnInfo(name = "protein")
    public int protein;

}
