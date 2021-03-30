package com.example.pocket_chef_application;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocket_chef_application.Pantry_utils.PantryTextWatcher;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Adapter;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Item;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Pantry extends Fragment {
    private EditText itemNameView, itemAmountView, itemEXPView;
    private android.widget.Button addItem, clearInput, camerabtn;
    PantryTextWatcher tw;

    private RecyclerView mRecyclerview;
    private Pantry_Adapter Padapter;
    private List<Pantry_Item> pantry_items;
    private FirebaseFirestore firebase_db;


    private final String TAG = "PANTRY";


    public static Pantry newInstance() {
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
        setOnClickListeners();
        return view;
    }

    private void getViews(View view){
        itemNameView = (EditText) view.findViewById(R.id.item_name);
        itemAmountView = (EditText) view.findViewById(R.id.item_amount);
        itemEXPView = (EditText) view.findViewById(R.id.item_exp);
        addItem = (android.widget.Button) view.findViewById(R.id.item_button);
        clearInput = (android.widget.Button) view.findViewById(R.id.clear);
        camerabtn = (android.widget.Button) view.findViewById(R.id.camerabtn);

        firebase_db = FirebaseFirestore.getInstance();


    }

    private void setOnClickListeners(){
        addItem.setOnClickListener(v -> NewItem());
        clearInput.setOnClickListener(v -> clearInputs());
        camerabtn.setOnClickListener(v -> barcode_scanner());


        tw = new PantryTextWatcher(itemEXPView);
        itemEXPView.addTextChangedListener(tw);

        itemEXPView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                NewItem();
                return handled;
            }
        });
    }

    private void barcode_scanner(){
        Intent i = new Intent(this.getContext(), Item_Recognition_Activity.class);
        startActivity(i);
    }

    private void clearInputs(){
        itemNameView.setText(null);
        itemAmountView.setText(null);
        itemEXPView.setText(null);
    }

    private void initRecyclerView(View view){
        mRecyclerview = view.findViewById(R.id.pantry_recyclerView);
        Padapter = new Pantry_Adapter(pantry_items,view.getContext());
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(view.getContext(),3));
        mRecyclerview.setAdapter(Padapter);
    }

    private void NewItem(){

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


        try {
            DBItem item = new DBItem();

            item.item_Name = name.toLowerCase();
            item.exp_date = exp_date.toLowerCase();
            item.amount = amount;
            int position = pantry_items.size();

            firebase_db.collection("food_warehouse")
                    .whereEqualTo("name",item.item_Name)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){


                            for(DocumentSnapshot doc : task.getResult()){
                                item.image_url = doc.getString("image");
                            }
                            db.itemDAO().insertItem(item);
                            pantry_items.add(position, new Pantry_Item(item));
                            Padapter.notifyItemInserted(position);

                        }
                    });






            /* TODO -- decided whether we want the app to work offline (ie. generate images for offline use). Would result in fewer calls to firebase but would take up memory of the device.
           */



        } catch (SQLiteConstraintException e) {
            Toast.makeText(this.getContext(), "Item already in Pantry", Toast.LENGTH_LONG).show();
        }
    }







}