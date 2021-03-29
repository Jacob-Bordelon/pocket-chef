package com.example.pocket_chef_application;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pocket_chef_application.Pantry_utils.PantryTextWatcher;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Adapter;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Item;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Pantry extends Fragment {
    private EditText itemNameView, itemAmountView, itemEXPView;
    private android.widget.Button addItem, clearInput, camerabtn;
    PantryTextWatcher tw;

    private RecyclerView mRecyclerview;
    private Pantry_Adapter Padapter;
    private List<Pantry_Item> pantry_items;
    private FirebaseFirestore firebase_db;
    private FirebaseStorage firebase_storage;
    private StorageReference fb_storageRef;
    private CollectionReference food_warehouse;

    private final String TAG = "PANTRY";


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
        camerabtn = (android.widget.Button) view.findViewById(R.id.camerabtn);

        firebase_db = FirebaseFirestore.getInstance();
        firebase_storage = FirebaseStorage.getInstance();
        fb_storageRef = firebase_storage.getReference();


    }

    private void setOnClickListeners(){
        addItem.setOnClickListener(v -> NewItem());
        clearInput.setOnClickListener(v -> clearInputs());
        camerabtn.setOnClickListener(v -> barcode_scanner());


        tw = new PantryTextWatcher(itemEXPView);
        itemEXPView.addTextChangedListener(tw);
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
        final int[] position = new int[1];

        try {
            DBItem item = new DBItem();

            item.item_Name = name.toLowerCase();
            item.exp_date = exp_date.toLowerCase();
            item.amount = amount;
            final Pantry_Item[] pItem = new Pantry_Item[1];

                    //TODO -- add image url column to local database so the firebase ImageUrl can be saved in the app.
            firebase_db.collection("food_warehouse")
                    .whereEqualTo("name", item.item_Name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(task.isSuccessful()){

                                    item.image_url = Objects.requireNonNull(document.getData().get("image")).toString();
                                    Log.d(TAG, "task completed - successful: "+item.image_url);
                                }else{
                                    Log.d(TAG, "task completed - unsuccessful");

                                }

                            }
                            db.itemDAO().insertItem(item);
                            pItem[0] = new Pantry_Item(item);
                            pItem[0].setImageUrl(item.image_url);
                            position[0] = pantry_items.size();
                            pantry_items.add(position[0], pItem[0]);
                            Padapter.notifyItemInserted(position[0]);
                        }
                    });
            /* TODO -- decided whether we want the app to work offline (ie. generate images for offline use). Would result in fewer calls to firebase but would take up memory of the device.
           */

        }
        catch (SQLiteConstraintException e) {
            Toast.makeText(this.getContext(), "Item already in Pantry", Toast.LENGTH_LONG).show();
        }
    }




}