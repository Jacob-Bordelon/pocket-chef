package com.example.pocket_chef_application.Gen_Recipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Firebase.FirebaseRecipeDatabase_Helper;
import com.example.pocket_chef_application.MainActivity;
import com.example.pocket_chef_application.Model.Ingredient;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.DBItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeAdapter {
    private static final String TAG = RecipeAdapter.class.getSimpleName();
    private static Context context;
    public Adapter recipeAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Recipe> recipes)
    {
        this.context = context;
        recipeAdapter = new Adapter(recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recipeAdapter);
    }

    public void refresh(){
        recipeAdapter.notifyDataSetChanged();
    }


    static class RecipeItemView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleView, prepView, cookView, totalView, diffView, authorView, descView;
        private ConstraintLayout hiddenView;
        private ImageView imageView;
        private CardView cardView;
        private RatingBar ratingBar;
        private ChipGroup filterView;
        private ImageButton report;

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
            report = (ImageButton) itemView.findViewById(R.id.report);

            itemView.setOnClickListener(this);

        }

        // using the same names from the constructor, set the values per each unique item here
        @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
        public void bind(Recipe recipe){
            Log.d(TAG, "bind: "+recipe.getTitle());
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

            report.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, itemView);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.elicit_image:
                        case R.id.elicit_text:
                        case R.id.irrelevant:
                            FirebaseRecipeDatabase_Helper.reportRecipe(recipe);
                            return true;
                        default:
                            return false;
                    }
                });
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.report_menu, popupMenu.getMenu());
                popupMenu.show();
            });
        }




        private void addChips(List<Ingredient> ingredients){
            filterView.removeAllViews();
            for(Ingredient i: ingredients){
                Log.d(TAG, "addChips: ");
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


    static class Adapter extends RecyclerView.Adapter<RecipeItemView>{
        public static List<Recipe> recipeList;
        private ArrayList<Recipe> backups;

        public Adapter(List<Recipe> recipeList) {
            Adapter.recipeList = recipeList;
            backups=new ArrayList<>();
            backups.addAll(recipeList);
        }

        @NonNull
        @Override
        public RecipeItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecipeItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeItemView holder, int position) {
            holder.bind(recipeList.get(position));
        }

        @Override
        public int getItemCount() {
            return recipeList.size();
        }


    }

    public void addfilter(List<DBItem> items){
        Adapter.recipeList.clear();
        Set<String> pantry_items = items.stream().map(o -> o.item_id).collect(Collectors.toSet());

         for(Recipe i: recipeAdapter.backups){
             boolean contains = i.getIngredientsId().stream().anyMatch(pantry_items::contains);
             if(contains) {
                 Adapter.recipeList.add(i);
             }
         }


        recipeAdapter.notifyDataSetChanged();
    }

    public void clearAllFilters(){
        Adapter.recipeList.clear();
        Adapter.recipeList.addAll(recipeAdapter.backups);
        recipeAdapter.notifyDataSetChanged();
    }

}
