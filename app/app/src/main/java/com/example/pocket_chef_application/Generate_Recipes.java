package com.example.pocket_chef_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Firebase.FirebaseRecipeDatabase_Helper;
import com.example.pocket_chef_application.Recipe_utils.Recipe_Adapter;
import com.example.pocket_chef_application.Recipe_utils.Recipe_Item;

import java.util.ArrayList;
import java.util.List;

public class Generate_Recipes extends Fragment {
    private TextView statusBar; // -> for displaying connection status
    private Recipe_Adapter Radapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Recipe_Item> recipes;
    private static final String TEXT = "text";

    public static Generate_Recipes newInstance(String text){
        Generate_Recipes fragment = new Generate_Recipes();
        Bundle args = new Bundle();
        args.putString(TEXT,text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initializes the view and the status bar on create for non null reference
        View view = inflater.inflate(R.layout.generate_recipes, container, false);
        statusBar = (TextView) view.findViewById(R.id.status_field);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.cookbook_recyclerview);
        new FirebaseRecipeDatabase_Helper().readRecipes(new FirebaseRecipeDatabase_Helper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Recipe_Item> recipes, List<String> keys) {
                new Recipe_Adapter(mRecyclerView, getContext(), recipes, keys);

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

        return view;
    }


}