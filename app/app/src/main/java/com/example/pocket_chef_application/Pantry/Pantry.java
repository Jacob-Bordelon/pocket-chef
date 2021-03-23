package com.example.pocket_chef_application.Pantry;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pocket_chef_application.ImageRecog;
import com.example.pocket_chef_application.ObjectDetection.CameraXLivePreviewActivity;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Pantry extends Fragment {
    private EditText itemNameView;
    private EditText itemAmountView;
    private EditText itemEXPView;
    private android.widget.Button addItem, clearInput, cameraBtn;
    PantryTextWatcher tw;

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
        getViews(view);

        LocalDB db = LocalDB.getDBInstance(this.getContext());
        List<DBItem> dbitems = db.itemDAO().getAllItems();
        pantry_items = dbitems.stream().map(Pantry_Item::new).collect(Collectors.toList());
        initRecyclerView(view);
        //insertDummyItems();

        setOnClickListeners();

        return view;
    }

    private void getViews(View view){
        itemNameView = (EditText) view.findViewById(R.id.item_name);
        itemAmountView = (EditText) view.findViewById(R.id.item_amount);
        itemEXPView = (EditText) view.findViewById(R.id.item_exp);
        addItem = (android.widget.Button) view.findViewById(R.id.item_button);
        clearInput = (android.widget.Button) view.findViewById(R.id.clear);
        cameraBtn = (android.widget.Button) view.findViewById(R.id.camerabtn);

    }

    private void setOnClickListeners(){
        addItem.setOnClickListener(v -> NewItem());
        clearInput.setOnClickListener(v -> clearInputs());
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ImageRecog.class));
            }
        });


        tw = new PantryTextWatcher(itemEXPView);
        itemEXPView.addTextChangedListener(tw);
    }

    private void clearInputs(){
        itemNameView.setText(null);
        itemAmountView.setText(null);
        itemEXPView.setText(null);
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
        itemEXPView.removeTextChangedListener(tw);
        clearInputs();
        itemEXPView.addTextChangedListener(tw);
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