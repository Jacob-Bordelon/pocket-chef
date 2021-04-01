package com.example.pocket_chef_application;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pocket_chef_application.Pantry_utils.Pantry_Adapter;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Item;
import com.example.pocket_chef_application.Pantry_utils.Searchbar_Adapter;
import com.example.pocket_chef_application.Pantry_utils.Suggested_Item;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pantry extends Fragment {
    private ImageButton  camerabtn, expand_menu_btn;
    private SearchView searchView;

    private Pantry_Adapter Padapter;
    private Searchbar_Adapter Sadapter;

    private List<Pantry_Item> pantry_items;
    private List<Suggested_Item> suggestion_items;
    private FirebaseFirestore firebase_db;
    private LinearLayout exanded_menu;
    private View rootView;
    private RecyclerView mRecyclerview;
    private ListView mListview;



    private final String TAG = "PANTRY";
    public static Pantry newInstance() {
        Pantry fragment = new Pantry();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.d("PANTRY", "New Instance ");
        return fragment;
    }

    
    // ------------------------- Lifecycle ---------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.pantry, container, false);
        getViews(view);

        LocalDB db = LocalDB.getDBInstance(this.getContext());
        List<DBItem> dbitems = db.itemDAO().getAllItems();
        pantry_items = dbitems.stream().map(Pantry_Item::new).collect(Collectors.toList());
        suggestion_items = new ArrayList<Suggested_Item>();
        suggestion_items.add(new Suggested_Item("apples"));
        suggestion_items.add(new Suggested_Item("cherries"));
        suggestion_items.add(new Suggested_Item("bananas"));

        initRecyclerView(view);
        initSearchView(view);
        setOnClickListeners();
        return view;
    }

    private void initSearchView(View view) {

    }

    // --------------------------- Functionality ---------------------
    private void getViews(View view){
        camerabtn = view.findViewById(R.id.camerabtn);
        firebase_db = FirebaseFirestore.getInstance();
        searchView = view.findViewById(R.id.searchView);
        expand_menu_btn = view.findViewById(R.id.expand_menu_btn);
        exanded_menu = view.findViewById(R.id.expanded_menu);
        rootView = view.findViewById(R.id.root_layout);
        mListview = view.findViewById(R.id.pantry_listview);

    }

    private void setOnClickListeners(){
        camerabtn.setOnClickListener(v -> barcode_scanner());
        setupUI(rootView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Padapter.filter(newText);
                return false;
            }
        });
        searchView.clearFocus();
        expand_menu_btn.setOnClickListener(v -> {
            if(exanded_menu.getVisibility() == View.GONE){
                exanded_menu.setVisibility(View.VISIBLE);
                expand_menu_btn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                camerabtn.setVisibility(View.VISIBLE);
                mListview.setVisibility(View.VISIBLE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

            }else {
                exanded_menu.setVisibility(View.GONE);
                expand_menu_btn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                camerabtn.setVisibility(View.GONE);
                mListview.setVisibility(View.GONE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Padapter.filter(newText);
                        return false;
                    }
                });


            }
        });

    }

    public void setupUI(View view) {

        if(!(view instanceof SearchView)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    searchView.clearFocus();
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }
    
    private void initRecyclerView(View view){
        mRecyclerview = view.findViewById(R.id.pantry_recyclerView);
        Padapter = new Pantry_Adapter(pantry_items,view.getContext());
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(view.getContext(),3));
        mRecyclerview.setAdapter(Padapter);
    }

    /*  private void NewItem(){

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
    }*/

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

    // Image Recognition
    private void barcode_scanner(){
        Intent i = new Intent(this.getContext(), Item_Recognition_Activity.class);
        startActivity(i);
    }




}