package com.example.pocket_chef_application.GroceryList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.GLItem;

import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.CustomViewHolder>  {
    private static final String TAG = GroceryList.class.getSimpleName();
    private Context context;
    private List<GLItem> itemList;

    public CustomListAdapter(Context context, List<GLItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item,parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.title.setText(itemList.get(position).item_Name);
        holder.amount.setText(Integer.toString(itemList.get(position).amount));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        TextView title, amount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {




        }
    }


}
