package com.example.pocket_chef_application.Recipe_utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Pantry_utils.Pantry_Item;
import com.example.pocket_chef_application.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class Recipe_Adapter extends RecyclerView.Adapter<Recipe_Adapter.RecipeItemView> {
    private Context context;
    private List<Recipe_Item> recipeList;
    private List<String> keysList;
    private Dialog mDialog;
    private final String TAG = "RECIPE_ADAPTER";

    public Recipe_Adapter(RecyclerView recyclerView, Context context, List<Recipe_Item> recipes, List<String> keys) {
        this.recipeList = recipes;
        this.keysList = keys;
        this.context = context;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(this);
    }

    @NonNull
    @Override
    public RecipeItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_item,parent, false);
        mDialog = new Dialog(view.getContext());
        return new RecipeItemView(parent);
    }

    @SuppressLint("SetTextI18n")
    public void ShowPopup(Recipe_Item item){
        TextView closebtn, name, RName, amount, measure, currStep, serving;
        RatingBar ratingBar;
        TableLayout ingredientTable, instructionTable;
        ImageView img;

        // Grab views from .xml
        mDialog.setContentView(R.layout.dialog_recipe_item_details);
        ingredientTable = (TableLayout) mDialog.findViewById(R.id.ingredient_table);
        instructionTable = (TableLayout) mDialog.findViewById(R.id.instructions_table);
        ratingBar = (RatingBar) mDialog.findViewById(R.id.ratingBar);
        name = mDialog.findViewById(R.id.item_name);
        img = (ImageView ) mDialog.findViewById(R.id.item_image);
        serving = (TextView) mDialog.findViewById(R.id.serving_size);
        closebtn = (TextView) mDialog.findViewById(R.id.closebtn);

        // set values and listeners in views
        name.setText(item.getRName());
        ratingBar.setRating(item.getRating());
        serving.setText(""+item.getServingSize());

        Log.d("Ingredient keys",""+item.getIngredients().keySet());
        for (String key : item.getIngredients().keySet()) {
            Map<String,Object> currIngredient = (Map<String,Object>)item.getIngredients().get(key);
            Log.d("Ingredient",": "+currIngredient);

            TableRow row = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            RName = new TextView(context);
            RName.setTextColor(Color.WHITE);
            RName.setText(currIngredient.get("name").toString());
            RName.setWidth(450);
            row.addView(RName);
            amount = new TextView(context);
            amount.setText(currIngredient.get("amount").toString());
            amount.setTextColor(Color.WHITE);
            amount.setWidth(80);
            row.addView(amount);
            measure = new TextView(context);
            measure.setText(currIngredient.get("measurement").toString());
            measure.setTextColor(Color.WHITE);
            measure.setWidth(80);
            row.addView(measure);

            ingredientTable.addView(row);
        }


        int numOfInstructions = item.getInstructions().size();
        Log.d("Instructions size",""+numOfInstructions);
        Log.d("Instructions values",""+item.getInstructions().values());

        for (int i = 1; i <numOfInstructions+1; i++) {

            TableRow row = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            currStep = new TextView(context);
            currStep.setTextColor(Color.WHITE);
            currStep.setText("Step "+i+": "+item.getInstructions().get("step"+i));
            row.addView(currStep);

            instructionTable.addView(row);
        }

        if(item.getImageUrl() != null){
            Picasso.get()
                    .load(item.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(img);
            Log.i(TAG, "Popup: Image loaded into dialog for-"+item.getRName());
        }
        else{
            Log.i(TAG, "Popup: No image loaded for-"+ item.getRName());
            img.setImageResource(R.drawable.no_image_found);
        }


        closebtn.setOnClickListener(v -> mDialog.dismiss());

        // display window
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeItemView holder, int position) {
        holder.bind(recipeList.get(position), keysList.get(position));

        if(recipeList.get(position).getImageUrl() != null){

            Picasso.get()
                    .load(recipeList.get(position).getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.recipeImageView);
            Log.i(TAG, "onBindViewHolder: Load image into adapter for- "+recipeList.get(position).getRName());
        }
        else{
            Log.i(TAG, "onBindViewHolder: No image found for: "+recipeList.get(position).getRName());
            holder.recipeImageView.setImageResource(R.drawable.no_image_found);
        }

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class RecipeItemView extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView title;
        private String key;
        ImageView recipeImageView;

        public RecipeItemView(ViewGroup parent){
            super(LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false));

            // Grab views by idea from recipe_item.xml using itemView.findViewById instead of view.findViewById
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            recipeImageView = (ImageView) itemView.findViewById(R.id.itemImageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ShowPopup(recipeList.get(getAdapterPosition()));

        }

        // using the same names from the constructor, set the values per each unique recipe_item here
        public void bind(Recipe_Item recipe, String key ){
            System.out.println(recipe);
            title.setText(recipe.getRName());
            this.key = key;
        }
    }

}
