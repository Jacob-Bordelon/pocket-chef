package com.example.pocket_chef_application.Pantry_utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Gen_Recipes.IngredientChip;
import com.example.pocket_chef_application.Item_Recognition_Activity;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.UploadActivity;
import com.example.pocket_chef_application.util.BackgroundThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFoodDB extends Fragment {

    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private ImageButton camerabtn;
    private final String TAG = "AddItemsToPantry";
    private FirebaseFoodDatabase_Helper helper;
    private BackgroundThread handlerThread = new BackgroundThread();

    @Override
    public void onPause() {
        super.onPause();
        handlerThread.quit();



    }

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
        searchView = (SearchView) view.findViewById(R.id.searchbar);

        camerabtn.setOnClickListener(v -> {
            image_recog();
        });
        helper = new FirebaseFoodDatabase_Helper();
        helper.setConfig(mRecyclerView,getContext());
        helper.defaultPage();

        RecyclerView.OnScrollListener scrollListner = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


                if (linearLayoutManager != null){
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == helper.getAdapterSize() - 1) {
                        helper.nextPage();
                    }

                    if(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 ){
                        //helper.previousPage();
                    }


                }


            }
        };
        mRecyclerView.addOnScrollListener(scrollListner);
        setupUI(view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Runnable run = new SearchRunnable(query, helper.adapter);
                handlerThread.getHandler().post(run);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.length() > 0){
                    helper.clearAdapterItems();
                }
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                mRecyclerView.removeOnScrollListener(scrollListner);
                helper.clearAdapterItems();
                if(!handlerThread.isAlive()){
                    handlerThread.start();
                }

            }else{
                if(searchView.getQuery().toString().isEmpty()){
                    helper.clearAdapterItems();
                    helper.defaultPage();
                    mRecyclerView.addOnScrollListener(scrollListner);
                }

            }
        });
        return view;
    }

    static class SearchRunnable implements Runnable {
        private static final String TAG = "SearchRunnable";
        private FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();
        private String query;
        private FirebaseFoodDatabase_Helper.FoodItemView adapter;

        SearchRunnable(final String query, final FirebaseFoodDatabase_Helper.FoodItemView adapter) {
            this.query = query;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            helper.searchFor(query, foods -> adapter.updateList(foods));
        }
    }



    private void image_recog(){
        Intent i = new Intent(this.getContext(), Item_Recognition_Activity.class);
        startActivity(i);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        if(!(view instanceof SearchView)) {
            view.setOnTouchListener((v, event) -> {
                searchView.clearFocus();
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.removeListener();
    }
}