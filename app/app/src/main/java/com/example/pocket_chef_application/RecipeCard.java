package com.example.pocket_chef_application;

import com.example.pocket_chef_application.Model.Recipe;

import java.util.ArrayList;

public class RecipeCard {

    private int imageResourse;
    private String title;
    private String desc;
    private ArrayList<String> ingredients;
    private boolean isShrink = true;

    public RecipeCard(int imageResourse, String title, String desc, ArrayList<String> ingredients) {
        this.imageResourse = imageResourse;
        this.title = title;
        this.desc = desc;
        this.ingredients = ingredients;
    }

    public int getImageResourse() {
        return imageResourse;
    }

    public void setImageResourse(int imageResourse) {
        this.imageResourse = imageResourse;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }
}