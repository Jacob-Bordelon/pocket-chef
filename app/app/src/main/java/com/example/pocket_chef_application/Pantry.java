package com.example.pocket_chef_application;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pantry extends Fragment {
    private EditText itemNameView;
    private EditText itemAmountView;
    private EditText itemEXPView;
    List<Pantry_Item> pantry_items;


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
        pantry_items = new ArrayList<>();
        refresh();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.pantry_recyclerView);
        Pantry_Adapter adapter = new Pantry_Adapter(pantry_items,view.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),3));
        recyclerView.setAdapter(adapter);



        view.findViewById(R.id.item_button).setOnClickListener(v -> refresh());


    }

    private void AddItem(){
        String title = itemNameView.getText().toString();
        Integer amount = Integer.parseInt(itemAmountView.getText().toString());
        String exp_date = itemEXPView.getText().toString();
        LocalDB db = LocalDB.getDBInstance(this.getContext());

        try {


            DBItem item = new DBItem();
            item.item_Name = title.toLowerCase();
            item.exp_date = exp_date.toLowerCase();
            item.amount = amount;
            db.itemDAO().insertItem(item);
            pantry_items.add(new Pantry_Item(item));


        }
        catch (SQLiteConstraintException e) {
            Toast.makeText(this.getContext(), "Item already in Pantry", Toast.LENGTH_LONG).show();
        }

        itemNameView.setText(null);
        itemEXPView.setText(null);
        itemAmountView.setText(null);

    }

    private void refresh(){
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        List<DBItem> dbitems = db.itemDAO().getAllItems();
        boolean found;
        for(DBItem item : dbitems){
            found = pantry_items.stream().anyMatch(o -> o.getItem().item_Name.equals(item.item_Name));
            if(!found){
                pantry_items.add(new Pantry_Item(item));
            }

        }
    }






    private void deleteAllItems() {
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        db.itemDAO().nukeTable();
    }
}