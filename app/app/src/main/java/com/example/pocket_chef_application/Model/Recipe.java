package com.example.pocket_chef_application.Model;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Recipe {
    private String RName;
    private String Instructions;
    private String Description;
    private Object Ingredients;

    public Recipe() {
    }

    public Recipe(String RName) {
        this.RName = RName;
    }

    public String getRName() {
        return RName;
    }

    public void setRName(String RName) {
        this.RName = RName;
    }

    public void setInstructions(String instructions) {
        Instructions = instructions;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setIngredients(Object ingredients) {
        Ingredients = ingredients;
    }

    public String getRecipe() {
        return RName;
    }

    public String getInstructions() {
        return Instructions;
    }

    public String getDescription() {
        return Description;
    }

    public ArrayList<String> getIngredients() {
        ArrayList<String> ingredients = (ArrayList<String>) Ingredients;
        return ingredients;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "RName='" + RName + '\'' +
                ", Instructions='" + Instructions + '\'' +
                ", Description='" + Description + '\'' +
                ", Ingredients=" + Ingredients +
                '}';
    }
}
