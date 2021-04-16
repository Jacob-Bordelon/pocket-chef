package com.example.pocket_chef_application.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Gen_Recipes.Ingredient;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.Pantry_utils.Suggested_Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRecipeDatabase_Helper {
    protected FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Recipe> recipes = new ArrayList<>();
    private final String TAG = "FirebaseRecipeHelper";

    public interface DataStatus{
        void DataIsLoaded(List<Recipe> recipes, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }



    public FirebaseRecipeDatabase_Helper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("recipeBook");


    }

    public void readRecipes(final DataStatus dataStatus){
        mReference
                .limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                List<String> keys = new ArrayList<>();

                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    recipe.setTitle(keyNode.getKey());


                    recipes.add(recipe);
                }
                dataStatus.DataIsLoaded(recipes, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readNextPage(final DataStatus dataStatus){

    }






}
