package com.example.pocket_chef_application.Firebase;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Model.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabase_Helper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Recipe> recipes = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Recipe> recipes, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabase_Helper() {
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
                    System.out.println(keyNode.getValue());

                    Recipe recipe = new Recipe(keyNode.getKey());
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
