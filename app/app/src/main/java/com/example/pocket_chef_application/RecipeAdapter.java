package com.example.pocket_chef_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter {
    private Context context;
    private Adapter recipeAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<Recipe> recipes, List<String> keys){
        this.context = context;
        recipeAdapter = new Adapter(recipes, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recipeAdapter);
    }


    class RecipeItemView extends RecyclerView.ViewHolder{
        private TextView title;
        private String key;

        public RecipeItemView(ViewGroup parent){
            super(LayoutInflater.from(context).inflate(R.layout.item, parent, false));

            // Grab views by idea from item.xml using itemView.findViewById instead of view.findViewById
            title = (TextView) itemView.findViewById(R.id.item_title);


        }

        // using the same names from the constructor, set the values per each unique item here
        public void bind(Recipe recipe, String key ){
            System.out.println(recipe);
            title.setText(recipe.getRName());
            this.key = key;
        }
    }


    class Adapter extends RecyclerView.Adapter<RecipeItemView>{
        private List<Recipe> recipeList;
        private List<String> keysList;

        public Adapter(List<Recipe> recipeList, List<String> keysList) {
            this.recipeList = recipeList;
            this.keysList = keysList;
        }

        @NonNull
        @Override
        public RecipeItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecipeItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeItemView holder, int position) {
            holder.bind(recipeList.get(position), keysList.get(position));
        }

        @Override
        public int getItemCount() {
            return recipeList.size();
        }
    }


}
