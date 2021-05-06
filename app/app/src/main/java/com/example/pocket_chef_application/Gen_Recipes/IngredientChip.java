package com.example.pocket_chef_application.Gen_Recipes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Ingredient;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.UploadActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class IngredientChip extends Chip {
    public static final List<IngredientChip> chips = new ArrayList<>();
    private static final String TAG = "IngredientChip";
    private CircularProgressDrawable progressDrawable;
    private Context context;
    private String name;
    private int amount;
    private String unit;
    private Ingredient ingredient;
    private String status;
    private String Foodid;
    private boolean del_tog;

    public IngredientChip(Context context, String name, int amount, String unit) {
        super(context, null, R.attr.CustomChipChoiceStyle);
        this.context = context;
        this.name = name;
        setText(name);
        this.amount = amount;
        this.unit = unit;
        this.ingredient = new Ingredient(amount,name,unit);
        this.progressDrawable  = new CircularProgressDrawable(context);
        this.del_tog = false;
        progressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
        progressDrawable.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.BLACK, BlendModeCompat.SRC_IN));
        progressDrawable.stop();
        setCloseIcon(context.getDrawable(R.drawable.ic_baseline_cancel_24));


        setPadding(3,0,3,0);
        setStatus("");
        chips.add(this);
    }

    public void setFoodList(List<Food> foods) {
        if(foods.size()<1){
            error("No Values found");
        }else if(foods.size()>30){
            error("Too many items found. Please be more specific.");
        }else{
            Log.d(TAG, "setFoodList: "+name);
            if(foods.size()==1){
                accepted(foods.get(0));
            }else{
                selected(foods);
            }

        }
    }

    public String getStatus() {
        return status;
    }

    public String getFoodId() {
        return Foodid;
    }

    public void showDialog(List<Food> foods){
        Dialog mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.dialog_select_item);
        TextView title = mDialog.findViewById(R.id.input_name);
        title.setText(name);
        LinearLayout layout = mDialog.findViewById(R.id.selection_layout);

        for(Food food:foods){
            View child = mDialog.getLayoutInflater().inflate(R.layout.selected_item, null);
            TextView name = child.findViewById(R.id.item_name);
            name.setText(food.getName());

            child.setOnClickListener(v -> {
                accepted(food);
                mDialog.dismiss();
            });

            layout.addView(child);
        }

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    public void toggleDelete(){
        if(!del_tog){
            setCloseIconVisible(true);
        }else{
            setCloseIconVisible(false);

        }
        del_tog = !del_tog;

    }

    public void makeDialogWork(List<Food> foodList){

    }

    public void error(String error){
        setStatus("error");
        setOnClickListener(v -> {
            if(UploadActivity.helper_text.getVisibility() == View.VISIBLE){
                UploadActivity.helper_text.setText(null);
                UploadActivity.helper_text.setVisibility(View.GONE);
            }else{
                UploadActivity.helper_text.setText(error);
                UploadActivity.helper_text.setVisibility(View.VISIBLE);
            }
        });
    }

    public void accepted(Food food){
        setStatus("accepted");
        setOnClickListener(null);
        setName(food.getName());
        Foodid = Integer.toString(food.getFdcId());
        ingredient.setName(food.getName());
    }

    public void selected(List<Food> foodList){
        setStatus("select");
        setOnClickListener(v -> {
            showDialog(foodList);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setStatus(String status){
        switch (status){
            case "select":
                setChipIcon(context.getDrawable(R.drawable.ic_action_needed));
                setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context ,R.color.good)));
                break;
            case "error":
                setChipIcon(context.getDrawable(R.drawable.ic_error_arose));
                setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context ,R.color.darkred)));
                break;
            case "accepted":
                setChipIcon(context.getDrawable(R.drawable.ic_all_good));
                setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context ,R.color.green)));
                break;
            default:
                setChipIcon(progressDrawable);
                progressDrawable.start();
                break;
        }
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        setText(name);
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

}
