package com.example.pocket_chef_application;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocket_chef_application.data.Item;
import com.example.pocket_chef_application.data.LocalDB;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText itemNameView;
    private EditText itemAmountView;
    private EditText itemEXPView;
    private TextView itemsView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_third, container, false);
        itemNameView = (EditText) view.findViewById(R.id.item_name);
        itemAmountView = (EditText) view.findViewById(R.id.item_amount);
        itemEXPView = (EditText) view.findViewById(R.id.item_exp);
        itemsView = (TextView) view.findViewById(R.id.items_view);

        getAllItems();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addNewItems(itemNameView.getText().toString(),itemEXPView.getText().toString(),Integer.parseInt(itemAmountView.getText().toString()));
                }
                catch (NumberFormatException nfe) { System.out.println("Could not parse "+nfe);}

            }
        });
    }
    private void getAllItems() {
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        List<Item> items = db.itemDAO().getAllItems();
        itemsView.setText(null);

        for (Item item : items) {
            String content = "";
            content += "Name: " + item.item_Name + "\n";
            content += "Amount: " + item.amount + "\n";
            content += "Expiration date: " + item.exp_date + "\n";
            content += "#############################" + "\n";

            itemsView.append(content);
        }
    }

    private void addNewItems(String name, String expDate, int amount) {
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        Item item = new Item();
        item.item_Name = name.toLowerCase();
        item.amount = amount;
        item.exp_date = expDate.toLowerCase();
        // add the item to the pantry. If duplicate, catch and handle
        try { db.itemDAO().insertItem(item); }
        catch (SQLiteConstraintException e) {
            Toast.makeText(this.getContext(), "Item already in Pantry", Toast.LENGTH_LONG).show();
            // ToDo: We can update the pantry if there is a conflict
        }
        itemNameView.setText(null);
        itemEXPView.setText(null);
        itemAmountView.setText(null);
    }

    private void deleteAllItems() {
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        db.itemDAO().nukeTable();
    }
}