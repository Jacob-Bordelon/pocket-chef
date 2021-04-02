package com.example.pocket_chef_application.Pantry_utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.R;

import java.util.ArrayList;
import java.util.List;

public class FoodItemAdapter {
    private Context context;
    public Adapter foodAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Food> items, List<String> keys){
        this.context = context;
        foodAdapter = new Adapter(items, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(foodAdapter);
    }


    class FoodItemView extends RecyclerView.ViewHolder{
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
    }


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

    public void filter(String item){
        Log.d("FOOD", "filter: "+foodAdapter.foodList.size());
        item = item.toLowerCase();


        if(item.length() == 0) {
            foodAdapter.foodList.clear();
        }else {
            for(Food i: foodAdapter.arrayList){
                if(i.getName().toLowerCase().startsWith(item)){
                    foodAdapter.foodList.add(i);
                }
            }
        }

        foodAdapter.notifyDataSetChanged();

    }



}
