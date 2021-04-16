package com.example.pocket_chef_application.Recipe_utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Gen_Recipes.Ingredient;
import com.example.pocket_chef_application.R;

import java.util.List;

public class Ingredient_Adapter extends RecyclerView.Adapter<Ingredient_Adapter.IngredientView>{
    private Context context;
    private List<Ingredient> ingredientsList;
    private RecyclerView recyclerView;

    public Ingredient_Adapter(Context context, RecyclerView recyclerView, List<Ingredient> ingredientsList) {
        this.context = context;
        this.ingredientsList = ingredientsList;
        this.recyclerView = recyclerView;
    }



    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(pos * 100);
    }

    @NonNull
    @Override
    public IngredientView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ingredient_view,parent, false);
        return new Ingredient_Adapter.IngredientView(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientView holder, int position) {



    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public class IngredientView extends RecyclerView.ViewHolder{

        private EditText title, amount;
        private Spinner units;
        private Button oper;

        public IngredientView(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.prompt);
            amount = itemView.findViewById(R.id.amount);
            units = itemView.findViewById(R.id.units);
            oper = itemView.findViewById(R.id.button5);

        }





    }
}
