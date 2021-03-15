package com.example.pocket_chef_application;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;
import com.example.pocket_chef_application.util.PantryItemTouchHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Pantry extends Fragment {
    private EditText itemNameView;
    private EditText itemAmountView;
    private EditText itemEXPView;
    private Button addItem;

    private RecyclerView mRecyclerview;
    private Pantry_Adapter Padapter;
    private List<Pantry_Item> pantry_items;


    public static Pantry newInstance(String param1, String param2) {
        Pantry fragment = new Pantry();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.pantry, container, false);

        itemNameView = (EditText) view.findViewById(R.id.item_name);
        itemAmountView = (EditText) view.findViewById(R.id.item_amount);
        itemEXPView = (EditText) view.findViewById(R.id.item_exp);
        addItem = (Button) view.findViewById(R.id.item_button);

        LocalDB db = LocalDB.getDBInstance(this.getContext());
        List<DBItem> dbitems = db.itemDAO().getAllItems();
        pantry_items = dbitems.stream()
                                .map(s -> new Pantry_Item(s))
                                .collect(Collectors.toList());
        initRecyclerView(view);
        insertDummyItems();
        addItem.setOnClickListener(v -> NewItem());

        return view;
    }

    private void insertDummyItems(){
        List<String> names = Arrays.asList("pears", "peaches", "cucumbers","tomatoes","milk","cheese","ground beef");

        for(String n : names){
            DBItem item = new DBItem();
            item.item_Name = n;
            item.exp_date = "12/12/2021";
            item.amount = 4;
            pantry_items.add(new Pantry_Item(item));
        }
    }

    private void initRecyclerView(View view){
        mRecyclerview = view.findViewById(R.id.pantry_recyclerView);
        Padapter = new Pantry_Adapter(pantry_items,view.getContext());
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(view.getContext(),3));


        mRecyclerview.setAdapter(Padapter);
    }

    private void NewItem(){
        System.out.println(LocalDB.getDBInstance(this.getContext()).itemDAO().getCount());

        if(     !(itemNameView.getText().toString().matches("")   |
                itemAmountView.getText().toString().matches("") |
                itemEXPView.getText().toString().matches("")))
        {
            AddItem(
                    itemNameView.getText().toString(),
                    Integer.parseInt(itemAmountView.getText().toString()),
                    itemEXPView.getText().toString()
            );
        }else{
            Toast.makeText(this.getContext(),"Values missing from input",Toast.LENGTH_LONG).show();
        }
        itemNameView.setText(null);
        itemAmountView.setText(null);
        itemEXPView.setText(null);
    }

    private void AddItem(String name, int amount, String exp_date){
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        int position;

        try {
            DBItem item = new DBItem();
            item.item_Name = name.toLowerCase();
            item.exp_date = exp_date.toLowerCase();
            item.amount = amount;
            db.itemDAO().insertItem(item);
            position = pantry_items.size();
            pantry_items.add(position,new Pantry_Item(item));
            Padapter.notifyItemInserted(position);

        }
        catch (SQLiteConstraintException e) {
            Toast.makeText(this.getContext(), "Item already in Pantry", Toast.LENGTH_LONG).show();
        }
    }




}