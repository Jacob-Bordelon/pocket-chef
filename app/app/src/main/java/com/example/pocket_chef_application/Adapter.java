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

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private RecyclerView flagrecyclerView;
    private ArrayList<IngredientFlag> ingflags = new ArrayList<>();
    private ArrayList<RecipeCard> recipeCards;
    private Context context;

    public Adapter(ArrayList<RecipeCard> recipeCards, Context context) {
        this.recipeCards = recipeCards;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card,
                parent,false);

        flagrecyclerView = view.findViewById(R.id.flag_recyclerView);
        flagrecyclerView.setHasFixedSize(true);
        flagrecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        flagrecyclerView.setAdapter(new IngredientAdapter(ingflags, view.getContext()));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, final int position) {

        final RecipeCard recipeCard = recipeCards.get(position);
        holder.imageView.setImageResource(recipeCard.getImageResourse());
        holder.titleTextView.setText(recipeCard.getTitle());
        holder.ingredientsTextView.setText(recipeCard.getDesc());

        for(String ing : recipeCard.getIngredients()){
            ingflags.add(new IngredientFlag(ing));
            if(ingflags.size() > 3) break;
        }



    }

    @Override
    public int getItemCount() {
        return recipeCards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView ingredientsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            //ingredientsTextView = itemView.findViewById(R.id.IngredientsTextView);
        }
    }
}
