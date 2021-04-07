package com.example.pocket_chef_application.Pantry_utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.Pantry;
import com.example.pocket_chef_application.R;

import java.util.ArrayList;
import java.util.List;

public class FoodItemAdapter {
    private final static String TAG = FoodItemAdapter.class.getSimpleName();
    private Context context;
    public Adapter foodAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Food> items, List<String> keys){
        this.context = context;
        foodAdapter = new Adapter(items, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(foodAdapter);
    }

    // Bind info to each food item card
    class FoodItemView extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private TextView name;
        private String key;

        public FoodItemView(ViewGroup parent){
            super(LayoutInflater.from(context).inflate(R.layout.suggestion_layout, parent, false));

            // Grab views by idea from item.xml using itemView.findViewById instead of view.findViewById
            name = (TextView) itemView.findViewById(R.id.item_name);

        }

        // using the same names from the constructor, set the values per each unique item here
        public void bind(Food food, String key ){
            name.setText(food.getName());
            this.key = key;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: ");
            showDialog(foodAdapter.foodList.get(getAdapterPosition()));
        }

        private void showDialog(Food food) {
            Dialog mDialog = new Dialog(context);
            mDialog.setContentView(R.layout.dialog_add_new_item);
            TextView name = mDialog.findViewById(R.id.item_name);
            TextView closebtn = mDialog.findViewById(R.id.closebtn);
            EditText amount = mDialog.findViewById(R.id.edititem_amount);
            EditText experation = mDialog.findViewById(R.id.edititem_exp);
            Button submit = mDialog.findViewById(R.id.submitbtn);

            closebtn.setOnClickListener(v->mDialog.dismiss());
            name.setText(food.getName());

            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.show();




        }
    }

    // attach card to recycler view and send info to foodItemView
    class Adapter extends RecyclerView.Adapter<FoodItemView>{
        private List<Food> foodList;
        private List<String> keysList;
        private ArrayList<Food> arrayList;

        public Adapter(List<Food> foodList, List<String> keysList) {
            this.foodList = foodList;
            this.keysList = keysList;

            this.arrayList = new ArrayList<>();
            this.arrayList.addAll(foodList);
        }

        @NonNull
        @Override
        public FoodItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FoodItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FoodItemView holder, int position) {
            holder.bind(foodList.get(position), keysList.get(position));
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

    }


    // Filter through items
    public void filter(String item){
        foodAdapter.foodList.clear();
        item = item.toLowerCase();

        if(item.length() != 0 ) {
            for(Food i: foodAdapter.arrayList){
                if(i.getName().toLowerCase().startsWith(item)){
                    foodAdapter.foodList.add(i);
                }
            }
        }

        foodAdapter.notifyDataSetChanged();

    }



}
