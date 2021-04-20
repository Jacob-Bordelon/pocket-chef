package com.example.pocket_chef_application.GroceryList;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.GLItem;
import com.example.pocket_chef_application.data.LocalDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class GroceryList extends Fragment {
    private static final String TAG = GroceryList.class.getSimpleName();
    private RecyclerView list;
    private LocalDB db;
    private List<GLItem> items;
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

        items = db.itemDAO().getAllGLItems();

        adapter = new CustomListAdapter(requireContext(), items);
        list = (RecyclerView) view.findViewById(R.id.listView);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Remove item from backing list here
                int position = viewHolder.getBindingAdapterPosition();
                removeGLItem(position);
                adapter.notifyDataSetChanged();
            }
        });

        //itemTouchHelper.attachToRecyclerView(list);

        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                NewGLItem();
                return super.onDoubleTap(e);
            }
        });

        LinearLayout layout = view.findViewById(R.id.linearLayout);
        View rowView = inflater.inflate(R.layout.new_list_item, null,true);
        layout.addView(rowView);

        layout.setOnTouchListener((v, event) -> { gestureDetector.onTouchEvent(event);return true; });

        view.findViewById(R.id.addGLItem).setOnClickListener(v -> NewGLItem());
        return view;
    }

    public void AddGLItem(String name, int amount){
        GLItem glItem = new GLItem();
        glItem.item_Name = name;
        glItem.amount = amount;
        glItem.item_id = UUID.randomUUID().toString();

        db.itemDAO().insertGLItem(glItem);
    }

    public void removeGLItem(int position){
        GLItem item = items.get(position);
        items.remove(item);

    }

    public void NewGLItem(){
        GLItem glItem = new GLItem();
        glItem.item_Name = "New Item "+(items.size()+1);
        glItem.amount = 2;

        items.add(glItem);
        adapter.notifyDataSetChanged();
    }


}