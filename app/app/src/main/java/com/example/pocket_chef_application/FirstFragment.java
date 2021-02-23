package com.example.pocket_chef_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FirstFragment extends Fragment {
    private static final String TAG = FirstFragment.class.getSimpleName();
    private static final String TEXT = "text";
    private ArrayList<Item> items = new ArrayList<>();


    int clickCount;

    private String mText;

    public static FirstFragment newInstance(String text){
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(TEXT,text);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        clickCount=0;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new Adapter(items,view.getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        items.add(new Item(R.drawable.eggs,"Eggs","they are whiter than a sharpee"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));
        items.add(new Item(R.drawable.bacon,"Bacon","Its Crackling Soap"));





    }
}