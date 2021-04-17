package com.example.pocket_chef_application.Firebase;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Model.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRecipeDatabase_Helper {
    protected FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Recipe> recipes = new ArrayList<>();
    private final String TAG = "FirebaseRecipeHelper";
    private ValueEventListener listener;
    private int limitAmount = 20;
    public String DEFAULT_PAGE_INDEX = "boiled egg";

    public void removeListeners() {
        mReference.removeEventListener(listener);
    }

    public interface DataStatus{
        void DataIsLoaded(List<Recipe> recipes);
    }

    public interface Data{
        void RetrievedData(List<Recipe> recipes, String nextPage);
    }

    public FirebaseRecipeDatabase_Helper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("recipeBook");


        listener = nullValueEventListener();


    }

    private ValueEventListener nullValueEventListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    //TODO - add update operation

    public void readRecipes(final DataStatus dataStatus){
        mReference.removeEventListener(listener);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();

                for(DataSnapshot keyNode : snapshot.getChildren()){
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                dataStatus.DataIsLoaded(recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mReference.limitToFirst(limitAmount).addValueEventListener(listener);
    }

    public void paginate(String page, final FirebaseRecipeDatabase_Helper.Data data){
        mReference.removeEventListener(listener);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                String nextpage = "";
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    recipe.setTitle(keyNode.getKey());
                    nextpage = keyNode.getKey();
                    recipes.add(recipe);
                }

                data.RetrievedData(recipes, nextpage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReference
                .orderByKey()
                .startAt(page)
                .limitToFirst(limitAmount)
                .addListenerForSingleValueEvent(listener);
    }






}
