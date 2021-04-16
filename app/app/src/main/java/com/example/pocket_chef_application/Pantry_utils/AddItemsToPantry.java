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
        camerabtn  = (ImageButton) view.findViewById(R.id.camerabtn);
        FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();
        helper.setConfig(mRecyclerView,getContext());
        helper.defaultPage();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


                if (linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                helper.getAdapterSize() - 1) {
                    //bottom of list!
                    helper.nextPage();

                }



            }
        });








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