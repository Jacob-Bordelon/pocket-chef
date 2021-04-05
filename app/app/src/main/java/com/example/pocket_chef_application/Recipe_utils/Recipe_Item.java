package com.example.pocket_chef_application.Recipe_utils;

import java.util.ArrayList;
import java.util.Map;

public class Recipe_Item {
    private String RName;
    private Object Instructions;
    private String Description;
    private String Difficulty;
    private Object Ingredients;
    private String Author;
    private float Rating;
    private int CookTime;
    private int PrepTime;
    private String Image;
    private int ServingSize;

    public Recipe_Item() {
    }

    public Recipe_Item(String RName) {
        this.RName = RName;
    }

    public void setRName(String RName) {
        this.RName = RName;
    }
    public void setDifficulty(String difficulty) {
        this.Difficulty = difficulty;
    }
    public void setInstructions(Object instructions) {
        Instructions = instructions;
    }
    public void setAuthor(String author) {
        this.Author = author;
    }
    public void setDescription(String description) {
        this.Description = description;
    }
    public void setCookTime(int cookTime) { this.CookTime = cookTime; };
    public void setPrepTime(int cookTime) { this.PrepTime = PrepTime; };;
    public void setIngredients(Object ingredients) {
        Ingredients = ingredients;
    }
    public void setRating(float rating) { Rating = rating; }
    public void setImageUrl(String image) { Image = image; }
    public void setServingSize(int servingSize) { ServingSize = servingSize; }

    public String getRName() {
        return RName;
    }
    public String getRecipe() {
        return RName;
    }
    public String getDifficulty() {
        return Difficulty;
    }
    public String getDescription() {
        return Description;
    }
    public String getAuthor() {
        return Author;
    }
    public String getImageUrl() {
        return Image;
    }
    public int getCookTime() {
        return CookTime;
    }
    public int getPrepTime() {
        return PrepTime;
    }
    public int getServingSize() { return ServingSize; }
    public float getRating() { return Rating; }
    public Map<String,Object> getInstructions() {
        Map<String,Object> instructions = (Map<String,Object>)Instructions;
        return instructions;
    }
    public Map<String,Object> getIngredients() {
        Map<String,Object> ingredients = (Map<String,Object>) Ingredients;
        return ingredients;
    }
}
