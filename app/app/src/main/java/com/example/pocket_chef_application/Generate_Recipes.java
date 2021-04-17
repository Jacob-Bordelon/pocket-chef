package com.example.pocket_chef_application;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Firebase.FirebaseRecipeDatabase_Helper;
import com.example.pocket_chef_application.Gen_Recipes.RecipeAdapter;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Item;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Generate_Recipes extends Fragment {
    // Textview variables
    private TextView filterButton, calCount, new_rec;
    private SeekBar calBar;
    private CheckBox usePantry;
    private ConstraintLayout filterMenu;
    private RecyclerView mRecyclerView;
    private FirebaseRecipeDatabase_Helper helper;
    private static final String TEXT = "text";
    private int min=10, max=100, current=10;

    public static Generate_Recipes newInstance(){
        Generate_Recipes fragment = new Generate_Recipes();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generate_recipes, container, false);
        filterMenu = (ConstraintLayout) view.findViewById(R.id.filterMenu);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.cookbook_recyclerview);
        RecipeAdapter adapter = new  RecipeAdapter();
        helper = new FirebaseRecipeDatabase_Helper();
        helper.readRecipes(recipes -> adapter.setConfig(mRecyclerView,getContext(), recipes));

        new_rec = view.findViewById(R.id.new_rec);
        new_rec.setOnClickListener(v ->{
            Intent i = new Intent(this.getContext(), UploadActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.nothing);
        });

        filterButton = view.findViewById(R.id.filterbtn);
        filterButton.setOnClickListener(v -> {
            if(filterMenu.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(filterMenu, new AutoTransition());
                filterMenu.setVisibility(View.GONE);
            }
            else {
                TransitionManager.beginDelayedTransition(filterMenu, new AutoTransition());
                filterMenu.setVisibility(View.VISIBLE);

            }
        });

        calBar = (SeekBar) view.findViewById(R.id.seekBar);
        calCount = (TextView) view.findViewById(R.id.calories_count);


        calBar.setMax(max-min);
        calBar.setProgress(current-min);
        calCount.setText(""+current);
        calBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current=progress+min;
                calCount.setText(""+current);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        usePantry = (CheckBox) view.findViewById(R.id.usepantry_checkBox);
        usePantry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                LocalDB db = LocalDB.getDBInstance(getContext());
                List<DBItem> dbitems = db.itemDAO().getAllItems();
                adapter.addfilter(dbitems);

            }else{
                adapter.clearAllFilters();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.removeListeners();

    }
}