package com.example.pocket_chef_application.Model;

public class Recipe {
    private String RName;
    private String Instructions;
    private int Amount;
    private String Measure;
    private String Ingredient;

    public String getRecipe() {
        return RName;
    }

    public String getInstructions() {
        return Instructions;
    }

    public int getAmount() {
        return Amount;
    }

    public String getMeasure() {
        return Measure;
    }

    public String getIngredient() {
        return Ingredient;
    }
}
