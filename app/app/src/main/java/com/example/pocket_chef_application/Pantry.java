package com.example.pocket_chef_application;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Pantry_utils.FoodItemAdapter;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Adapter;
import com.example.pocket_chef_application.Pantry_utils.Pantry_Item;
import com.example.pocket_chef_application.Pantry_utils.AddItemsToPantry;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;


import java.util.List;
import java.util.stream.Collectors;

public class Pantry extends Fragment {
    private ImageButton  camerabtn, expand_menu_btn;
    private SearchView searchView;
    private TextView statusVal;

    private static Pantry_Adapter Padapter;
    private FoodItemAdapter Sadapter;
    private static Context context;

    private static List<Pantry_Item> pantry_items;
    private ConstraintLayout exanded_menu;
    private View rootView;
    private RecyclerView mRecyclerview, suggestionsView;
    private FirebaseFoodDatabase_Helper helper;



    private final String TAG = "PANTRY";
    public static Pantry newInstance() {
        Pantry fragment = new Pantry();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.d("PANTRY", "New Instance ");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        context = this.getContext();
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
        initSearchView(view);
        setOnClickListeners();
        return view;
    }

    private void initSearchView(View view) {
        suggestionsView = view.findViewById(R.id.pantry_suggestions);
        Sadapter = new FoodItemAdapter();
        /*dataStatus = new FirebaseFoodDatabase_Helper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Food> foods, List<String> keys) {
                Sadapter.setConfig(suggestionsView, getContext(), foods, keys);
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
        };*/
        helper = new FirebaseFoodDatabase_Helper();
    }

    // --------------------------- Functionality ---------------------
    private void getViews(View view){
        camerabtn = view.findViewById(R.id.camerabtn);
        searchView = view.findViewById(R.id.searchView);
        expand_menu_btn = view.findViewById(R.id.expand_menu_btn);
        exanded_menu = view.findViewById(R.id.expanded_menu);
        rootView = view.findViewById(R.id.pantryFragment);
        statusVal = view.findViewById(R.id.signal);

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

    public static void AddItem(Food food, String exp_date, int amount){
        LocalDB db = LocalDB.getDBInstance(context);
        try {
            DBItem item = new DBItem();

            item.item_Name = food.getName().toLowerCase();
            item.exp_date = exp_date.toLowerCase();
            item.amount = amount;
            item.item_id = Integer.toString(food.getFdcId());
            item.image_url = food.getImage();
            int position = pantry_items.size();

            db.itemDAO().insertItem(item);
            pantry_items.add(position,new Pantry_Item(item));
            Padapter.notifyItemInserted(position);

        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, "Item already in Pantry", Toast.LENGTH_LONG).show();
        }
    }

    // Image Recognition
    private void barcode_scanner(){
        Intent i = new Intent(this.getContext(), Item_Recognition_Activity.class);
        startActivity(i);
    }
}