package com.example.pocket_chef_application.Model;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Food {
    private int fdcId;
    private String name;
    private String fullName;
    private String category;
    private String image;
    private int calories, carbs, fats, chols, protein, sodium, trans_fat, sat_fat, sugars, fiber;
    private HashMap<String,Integer> foodNutrients;

    public Food() {
    }

    public Food(int fdcId, String name, String fullName, String category, String image, HashMap<String, Integer> foodNutrients) {
        this.fdcId = fdcId;
        this.name = name;
        this.fullName = fullName;
        this.category = category;
        this.image = image;
        this.foodNutrients = foodNutrients;
        this.sodium = foodNutrients.get("sodium");
        this.calories = foodNutrients.get("calories");
        this.carbs = foodNutrients.get("carbs");
        this.chols = foodNutrients.get("chloesterol");
        this.protein = foodNutrients.get("protein");
        this.fats = foodNutrients.get("total_fat");
        this.trans_fat = foodNutrients.get("trans_fat");
        this.sat_fat = foodNutrients.get("sat_fat");
        this.sugars = foodNutrients.get("sugars");
        this.fiber =foodNutrients.get("fiber");

    }

    public void setFoodNutrients(HashMap<String, Integer> foodNutrients) {
        this.foodNutrients = foodNutrients;
        this.carbs = foodNutrients.get("carbs");
        this.sodium = foodNutrients.get("sodium");
        this.chols = foodNutrients.get("chloesterol");
        this.fats = foodNutrients.get("total_fat");
        this.sat_fat = foodNutrients.get("sat_fat");
        this.trans_fat = foodNutrients.get("trans_fat");
        this.calories = foodNutrients.get("calories");
        this.fiber = foodNutrients.get("fiber");
        this.sugars = foodNutrients.get("sugars");
    }

    public int getFdcId() {
        return fdcId;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public int getCalories() {
        return calories;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFats() {
        return fats;
    }

    public int getChols() {
        return chols;
    }

    public int getProtein() {
        return protein;
    }

    public int getSodium() {
        return sodium;
    }

    public int getTrans_fat() {
        return trans_fat;
    }

    public int getSat_fat() {
        return sat_fat;
    }

    public int getSugars() {
        return sugars;
    }

    public int getFiber() {
        return fiber;
    }

    public HashMap<String, Integer> getFoodNutrients() {
        return foodNutrients;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        String result = String.format("\nName: %s\nFdcId: %d\nNutrients:\n ", this.name, this.fdcId);
        result = result+String.format(
                "\tCalories: %d\n" +
                "\tTotal Fat: %d\n" +
                "\t\tSaturated Fat: %d\n" +
                "\t\tTrans Fat: %d\n" +
                "\tChloesterol: %d\n" +
                "\tSodium: %d\n"+
                "\tTotal Carbohydrate: %d\n" +
                "\t\tFiber: %d\n" +
                "\t\tSugars: %d\n" +
                "\tProtein: %d\n"
        ,getCalories(), getFats(), getSat_fat(), getTrans_fat(), getChols(), getSodium(), getCarbs(), getFiber(), getSugars(), getProtein());
        return result;
    }
}
