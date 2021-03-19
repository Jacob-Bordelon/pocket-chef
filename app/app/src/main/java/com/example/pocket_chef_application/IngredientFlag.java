package com.example.pocket_chef_application;

public class IngredientFlag {

    private String title;
    private boolean isShrink = true;

    public IngredientFlag(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }
}