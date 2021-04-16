package com.example.pocket_chef_application.Firebase;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class FirebaseFoodDatabase_Helper {
    private static final String TAG = "FirebaseFoodDatabase_Helper";
    public FirebaseDatabase mDatabase;
    public static String DEFAULT_PAGE_INDEX = "325871";
    private static DatabaseReference mReference;
    private Context context;
    private RecyclerView recyclerView;
    private List<Food> foodList = new ArrayList<>();
    public String gotoNextPage = "";
    public FoodItemView adapter;
    private int limitAmount = 10;
    private List<Integer> itemsFdcIds;

    public static String key;

    public interface DataStatus{
        void DataIsLoaded(List<Food> foods, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public interface Data{
        void RetrievedData(List<Food> foods, String nextPage);
    }

    public FirebaseFoodDatabase_Helper() {

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("food");

    }

    public void readFood(final DataStatus dataStatus){
        mReference
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodList.clear();
                        List<String> keys = new ArrayList<>();


                        for(DataSnapshot keyNode : snapshot.getChildren()){
                            key = keyNode.getKey();
                            Food food = keyNode.getValue(Food.class);
                            keys.add(key);
                            foodList.add(food);
                        }
                        dataStatus.DataIsLoaded(foodList, keys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void paginate(String i, final Data data){
        mReference
                .orderByKey()
                .startAt(i)
                .limitToFirst(limitAmount)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodList.clear();
                        String nextpage = "";
                        for(DataSnapshot keyNode : snapshot.getChildren()){
                            Food food = keyNode.getValue(Food.class);
                            foodList.add(food);
                            nextpage = keyNode.getKey();
                        }

                        data.RetrievedData(foodList, nextpage);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void defaultPage(){
        paginate(DEFAULT_PAGE_INDEX, (foods, nextPage) -> {
            adapter.updateList(foods);
            gotoNextPage = nextPage;
        });
    }

    public void nextPage(){
        paginate(gotoNextPage, (foods, nextPage) -> {
            adapter.updateList(foods);
            gotoNextPage = nextPage;
        });
    }


    // Custom Firebase Specific Recycler View Class
    public void setConfig(RecyclerView recyclerView, Context context){
        this.context = context;
        this.recyclerView = recyclerView;
        itemsFdcIds = new ArrayList<>();
        adapter = new FoodItemView();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);


    }

    public int getAdapterSize(){
        return adapter.items.size();
    }


    public class FoodItemView extends RecyclerView.Adapter<FoodItemView.FoodItemHolder>{
        List<Food> items = new ArrayList<>();

        public FoodItemView(List<Food> items) {
            this.items = items;

        }

        public FoodItemView() {
        }

        public void updateList(List<Food> foods){
            List<Food> foodItems = foodList.stream()
                    .collect(Collectors.toList());

            Log.d(TAG, "updateList: "+foodItems.size()+" "+items.size()+" "+foods.size());

            if(foodItems.size() >= 1){
                Log.d(TAG, "updateList: "+foods.size());
                items.addAll(foodItems);
            }
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FoodItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FoodItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FoodItemHolder holder, int position) {
            holder.name.setText(items.get(position).getName());

        }

        @Override
        public int getItemCount() {
            return items.size();
        }



        class FoodItemHolder extends RecyclerView.ViewHolder {
            private TextView name;

            public FoodItemHolder(ViewGroup parent){
                super(LayoutInflater.from(context).inflate(R.layout.suggestion_layout, parent, false));

                // Grab views by idea from item.xml using itemView.findViewById instead of view.findViewById
                name = (TextView) itemView.findViewById(R.id.item_name);

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


    }


}
