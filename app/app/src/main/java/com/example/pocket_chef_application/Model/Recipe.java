package com.example.pocket_chef_application.Model;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Recipe {
    private String RName;
    private String Instructions;
    private String Description;
    private Object Ingredients;

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
}
