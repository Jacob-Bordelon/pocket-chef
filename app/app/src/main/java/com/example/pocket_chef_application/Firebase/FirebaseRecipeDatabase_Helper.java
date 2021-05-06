package com.example.pocket_chef_application.Firebase;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Gen_Recipes.RecipeAdapter;
import com.example.pocket_chef_application.MainActivity;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Ingredient;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.DBItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class FirebaseRecipeDatabase_Helper {
    protected FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;
    private List<Recipe> recipes = new ArrayList<>();
    private final String TAG = "FirebaseRecipeHelper";
    private ValueEventListener listener;
    private int limitAmount = 20;
    public String DEFAULT_PAGE_INDEX = "boiled egg";
    private static Context context;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;


    public interface DataStatus{
        void DataIsLoaded(List<Recipe> recipes);
    }

    public interface Data{
        void RetrievedData(List<Recipe> recipes, String nextPage);
    }

    public FirebaseRecipeDatabase_Helper(RecyclerView recyclerView, Context context) {
        mDatabase = MainActivity.realtimedb;
        mReference = mDatabase.getReference("recipeBook/");
        Log.d(TAG, "FirebaseRecipeDatabase_Helper: "+mReference);
        listener = nullValueEventListener();
        this.context = context;
        this.recyclerView = recyclerView;
        this.adapter = new RecipeAdapter();
    }

    public void populate(){
        Log.d(TAG, "populate: ");
        readRecipes(recipes -> {
            adapter.setConfig(recyclerView, context, recipes);
            Log.d(TAG, "populate: "+recipes.size());
            adapter.refresh();
        });
    }

    public void addFilter(List<DBItem> items){
        adapter.addfilter(items);
    }

    public void clearFilters(){
        adapter.clearAllFilters();
    }

    public void removeListeners() {
        mReference.removeEventListener(listener);
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

    public void readRecipes(final DataStatus dataStatus){
        mReference.removeEventListener(listener);
        listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    recipes.add(recipe);
                    Log.d(TAG, "onDataChange: "+recipe.getTitle());
                }


                dataStatus.DataIsLoaded(recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mReference
                .orderByChild("status")
                .equalTo(0)
                .limitToFirst(limitAmount)
                .addValueEventListener(listener);
    }

    public static void reportRecipe(Recipe recipe){
        mReference.child(recipe.getId()).child("status").setValue(1);
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
                .orderByChild("status")
                .equalTo(0)
                .orderByKey()
                .startAt(page)
                .limitToFirst(limitAmount)
                .addListenerForSingleValueEvent(listener);
    }









}
