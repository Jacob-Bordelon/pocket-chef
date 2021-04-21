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
    private DatabaseReference mReference;
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
        listener = nullValueEventListener();
        this.context = context;
        this.recyclerView = recyclerView;
        this.adapter = new RecipeAdapter();
    }

    public void populate(){
        readRecipes(recipes -> {
            adapter.setConfig(recyclerView, context, recipes);
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

    /*--------------------- Recycler View Stuff --------------------------------*/

  /*  public void setConfig(RecyclerView recyclerView, Context context, List<Recipe> recipes)
    {
        this.context = context;
        recipeAdapter = new FirebaseRecipeDatabase_Helper.Adapter(recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recipeAdapter);
    }

    public void updateRecipes(){
        recipeAdapter.notifyDataSetChanged();
    }

    static class RecipeItemView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleView, prepView, cookView, totalView, diffView, authorView, descView;
        private ConstraintLayout hiddenView;
        private ImageView imageView;
        private CardView cardView;
        private RatingBar ratingBar;
        private ChipGroup filterView;

        public RecipeItemView(ViewGroup parent){
            super(LayoutInflater.from(context).inflate(R.layout.recipe_layout, parent, false));
            cardView = itemView.findViewById(R.id.card_base);
            hiddenView = itemView.findViewById(R.id.hidden_view);

            // Grab views by idea from item.xml using itemView.findViewById instead of view.findViewById
            titleView = (TextView) itemView.findViewById(R.id.rec_title);
            prepView = (TextView) itemView.findViewById(R.id.rec_prep);
            cookView = (TextView) itemView.findViewById(R.id.rec_cook);
            totalView = (TextView) itemView.findViewById(R.id.rec_total);
            authorView= (TextView) itemView.findViewById(R.id.rec_author);
            diffView= (TextView) itemView.findViewById(R.id.rec_difficulty);
            descView= (TextView) itemView.findViewById(R.id.rec_desc);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rec_rating);
            filterView = (ChipGroup) itemView.findViewById(R.id.filterChipGroup);
            imageView = (ImageView) itemView.findViewById(R.id.rec_img);

            itemView.setOnClickListener(this);

        }

        // using the same names from the constructor, set the values per each unique item here
        @SuppressLint("SetTextI18n")
        public void bind(Recipe recipe){
            titleView.setText(recipe.getTitle());
            prepView.setText(Integer.toString(recipe.getPrep_time()));
            cookView.setText(Integer.toString(recipe.getCook_time()));
            totalView.setText(Integer.toString(recipe.getPrep_time()+recipe.getCook_time()));
            authorView.setText(recipe.getAuthor());
            diffView.setText(recipe.getDifficulty());
            descView.setText(recipe.getDescription());
            ratingBar.setRating(recipe.getRating());
            addChips(new ArrayList<>(recipe.getIngredients().values()));


            if((recipe.getImage() != null ) && recipe.getImage().contains("https://firebasestorage.googleapis.com")){
                Picasso.get()
                        .load(recipe.getImage())
                        .fit()
                        .centerCrop()
                        .into(imageView);
            } else{
                imageView.setImageResource(R.drawable.no_image_found);
            }
        }


        private void addChips(List<Ingredient> ingredients){
            filterView.removeAllViews();
            for(Ingredient i: ingredients){
                Chip chip = new Chip(itemView.getContext());
                chip.setText(i.getFilterName());
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
                chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                filterView.addView(chip);
            }
        }

        @Override
        public void onClick(View v) {
            if(hiddenView.getVisibility() == View.VISIBLE){
                hiddenView.setVisibility(View.GONE);
            }
            else {
                hiddenView.setVisibility(View.VISIBLE);
            }
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
        }
    }


    static class Adapter extends RecyclerView.Adapter<FirebaseRecipeDatabase_Helper.RecipeItemView>{
        public static List<Recipe> recipeList;
        private ArrayList<Recipe> backups;

        public Adapter(List<Recipe> recipeList) {
            FirebaseRecipeDatabase_Helper.Adapter.recipeList = recipeList;
            backups=new ArrayList<>();
            backups.addAll(recipeList);
        }

        @NonNull
        @Override
        public FirebaseRecipeDatabase_Helper.RecipeItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FirebaseRecipeDatabase_Helper.RecipeItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FirebaseRecipeDatabase_Helper.RecipeItemView holder, int position) {
            holder.bind(recipeList.get(position));
        }

        @Override
        public int getItemCount() {
            return recipeList.size();
        }


    }
*/







}
