package com.example.pocket_chef_application.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseFoodDatabase_Helper {
    private static final String TAG = "FirebaseFoodDatabase_Helper";
    public FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;
    private List<Food> foodList = new ArrayList<>();

    public static String key, page;

    public interface DataStatus{
        void DataIsLoaded(List<Food> foods, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseFoodDatabase_Helper() {

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("food");
        page = "000001";

    }

    public void readFood(final DataStatus dataStatus){
        mReference
                .orderByKey()
                .startAfter(page)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodList.clear();
                        List<String> keys = new ArrayList<>();


                        for(DataSnapshot keyNode : snapshot.getChildren()){
                            Log.d(TAG, "onDataChange: "+keyNode.getKey());
                            key = keyNode.getKey();
                            Food food = keyNode.getValue(Food.class);
                            foodList.add(food);
                        }
                        dataStatus.DataIsLoaded(foodList, keys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void getFood(final DataStatus dataStatus, String str){
        mReference
                .child("name")
                .startAt(0)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodList.clear();
                        List<String> keys = new ArrayList<>();
                        Log.d(TAG, "onDataChange: "+snapshot.getChildrenCount());


                        for(DataSnapshot keyNode : snapshot.getChildren()){
                            keys.add(keyNode.getKey());
                            if(Objects.requireNonNull(keyNode.getKey()).toLowerCase().startsWith(str.toLowerCase()) && !str.equals("")){
                                Food food = new Food();
                                food.setName(keyNode.getKey());
                                foodList.add(food);
                            }

                        }
                        dataStatus.DataIsLoaded(foodList, keys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
