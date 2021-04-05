package com.example.pocket_chef_application.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Recipe_utils.Recipe_Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseRecipeDatabase_Helper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Recipe_Item> recipes = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Recipe_Item> recipes, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }



    public FirebaseRecipeDatabase_Helper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("recipeBook");
    }

    public void readRecipes(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Map<String,Object> recipeValues = (Map<String,Object>)keyNode.getValue();

                    Recipe_Item recipe = new Recipe_Item(keyNode.getKey());
                    recipe.setDifficulty(recipeValues.get("difficulty").toString());
                    recipe.setAuthor(recipeValues.get("author").toString());
                    recipe.setCookTime(Integer.parseInt(recipeValues.get("cook_time").toString()));
                    recipe.setDescription(recipeValues.get("description").toString());
                    recipe.setPrepTime(Integer.parseInt(recipeValues.get("prep_time").toString()));
                    recipe.setInstructions(recipeValues.get("instructions"));
                    recipe.setIngredients(recipeValues.get("ingredients"));
                    recipe.setRating(Float.parseFloat(recipeValues.get("rating").toString()));
                    recipe.setImageUrl(recipeValues.get("image").toString());

                    System.out.println(recipe == null);
                    recipes.add(recipe);
                }
                dataStatus.DataIsLoaded(recipes, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}
