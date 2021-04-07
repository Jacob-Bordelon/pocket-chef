package com.example.pocket_chef_application.Model;

import android.util.Log;

import com.example.pocket_chef_application.Gen_Recipes.Ingredient;
import com.example.pocket_chef_application.MainActivity;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Recipe {

    private String title;
    private String author;
    private String description;
    private int id;
    private int cook_time;
    private int prep_time;
    private int rating;
    private String difficulty;
    private String image;
    private HashMap<String, String> instructions;
    private HashMap<String, Ingredient> ingredients;



    public Recipe() {
    }

    public Recipe(String title, String author, String description, int id, int cook_time, int prep_time, int rating, String difficulty) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.id = id;
        this.cook_time = cook_time;
        this.prep_time = prep_time;
        this.rating = rating;
        this.difficulty = difficulty;
    }

    public Recipe(String author, String description, int id, int cook_time, int prep_time, int rating, String difficulty, String image, HashMap<String, String> instructions, HashMap<String, Ingredient> ingredients) {
        this.author = author;
        this.description = description;
        this.id = id;
        this.cook_time = cook_time;
        this.prep_time = prep_time;
        this.rating = rating;
        this.difficulty = difficulty;
        this.image = image;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public HashMap<String, Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<String> getIngredientsId(){
        if(ingredients != null){
            return ingredients.keySet();
        }

        return Collections.emptySet();
    }

    public HashMap<String, String> getInstructions() {
        return instructions;
    }

    public void setInstructions(HashMap<String, String> instructions) {
        this.instructions = instructions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCook_time() {
        return cook_time;
    }

    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
    }

    public int getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(int prep_time) {
        this.prep_time = prep_time;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
