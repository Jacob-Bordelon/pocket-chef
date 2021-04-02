package com.example.pocket_chef_application.Firebase;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFoodDatabase_Helper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Food> foodList = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Food> foods, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }



    public FirebaseFoodDatabase_Helper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("food");
    }

    public void readFoods(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Food food = new Food();
                    food.setName(keyNode.getKey());
                    foodList.add(food);
                }
                dataStatus.DataIsLoaded(foodList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}
