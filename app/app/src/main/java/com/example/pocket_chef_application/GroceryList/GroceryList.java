package com.example.pocket_chef_application.GroceryList;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.LocalDB;

import java.util.List;
import java.util.stream.Collectors;


public class GroceryList extends Fragment {
    private static final String TAG = GroceryList.class.getSimpleName();
    private RecyclerView list;
    public static LocalDB db;
    private List<GroceryItem> items;
    public static ConstraintLayout add_menu, edit_menu;
    private Button done, cancel, add;
    private EditText edit_amount, edit_title;
    CustomListAdapter adapter;

    public GroceryList() {
        // Required empty public constructor
    }

    public static GroceryList newInstance() {
        GroceryList fragment = new GroceryList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = LocalDB.getDBInstance(getContext());
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        items = db.GlDAO().getAllGLItems().stream().map(GroceryItem::new).collect(Collectors.toList());

        adapter = new CustomListAdapter(requireContext(), items);

        list = (RecyclerView) view.findViewById(R.id.listView);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setItemAnimator(new DefaultItemAnimator());
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        edit_title = view.findViewById(R.id.title);
        edit_amount = view.findViewById(R.id.editText);
        add = view.findViewById(R.id.addGLItem);
        add.setOnClickListener(v -> NewGLItem());

        return view;
    }

    public void AddGLItem(String name, int amount){
        GroceryItem glItem = new GroceryItem(name, amount);

        items.add(glItem);
        db.GlDAO().insertItem(glItem.getItem());
    }

    public void removeGLItem(GroceryItem item){
        items.remove(item);
        db.GlDAO().deleteItem(item.getItem());
    }

    public void NewGLItem(){
        if(!(edit_title.getText().toString().matches("") || edit_amount.getText().toString().matches(""))){
            String name = edit_title.getText().toString();
            int amount = Integer.parseInt(edit_amount.getText().toString());
            AddGLItem(name,amount);
            adapter.notifyDataSetChanged();
            edit_title.setText(null);
            edit_amount.setText(null);
        }


    }


}