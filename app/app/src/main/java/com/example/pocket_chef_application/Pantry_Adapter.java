package com.example.pocket_chef_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Pantry_Adapter extends RecyclerView.Adapter<Pantry_Adapter.ViewHolder> {
    private ArrayList<Pantry_Item> items;
    private Context context;

    public Pantry_Adapter(ArrayList<Pantry_Item> items, Context context){
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public Pantry_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Pantry_Adapter.ViewHolder holder, final int position){
        final Pantry_Item item = items.get(position);
        holder.titleTextView.setText(item.getTitle());
    }

    @Override
    public int getItemCount(){return items.size();}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}
