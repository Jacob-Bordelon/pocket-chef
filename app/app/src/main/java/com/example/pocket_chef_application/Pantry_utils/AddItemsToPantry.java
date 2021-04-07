package com.example.pocket_chef_application.Pantry_utils;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Item_Recognition_Activity;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.R;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddItemsToPantry extends Fragment {

    private RecyclerView mRecyclerView;
    private ItemsRecyclerView adapter;
    private ImageButton back_btn, camerabtn;
    private final String TAG = "AddItemsToPantry";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_items_to_pantry, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        back_btn = (ImageButton) view.findViewById(R.id.back_btn);
        camerabtn  = (ImageButton) view.findViewById(R.id.camerabtn);
        FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();
        helper.readFood(new FirebaseFoodDatabase_Helper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Food> foods, List<String> keys) {
                adapter = new ItemsRecyclerView(foods,getContext());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(adapter);

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        back_btn.setOnClickListener(v-> getActivity().onBackPressed());
        camerabtn.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: ");
            image_recog();
        });


        return view;


    }

    private void image_recog(){
        Intent i = new Intent(this.getContext(), Item_Recognition_Activity.class);
        startActivity(i);
    }



}