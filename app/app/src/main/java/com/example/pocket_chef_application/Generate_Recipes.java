package com.example.pocket_chef_application;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Firebase.FirebaseRecipeDatabase_Helper;
import com.example.pocket_chef_application.Recipe_utils.Recipe_Adapter;
import com.example.pocket_chef_application.Recipe_utils.Recipe_Item;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Switch switchPossible = (Switch) view.findViewById(R.id.possible);
        switchPossible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                Log.i("GENERATE RECIPE",""+isChecked);
                String word = "Apples, fuji, with skin, raw";
                String[] lines = {"Apples, gala, with skin, raw","Apples, fuji, with skin, raw","Apples, honeycrisp, with skin, raw","Apples, granny smith, with skin, raw","Apples, red delicious, with skin, raw"};

                int highest = 0;
                String match = "";
                for (String line:lines
                ) {
                    int iTemp = searchWords(word,line);
                    if(highest < iTemp) {
                        highest = iTemp;
                        match = line;
                    }
                }

                System.out.println(match);
            }
        });

    }

    public int searchWords(String line, String lin2) {
        String[] array = line.split(", ");

        Trie trie = Trie.builder().ignoreCase().addKeywords(array).build();
        Collection<Emit> emits = trie.parseText(lin2);

        return emits.size();
    }
}