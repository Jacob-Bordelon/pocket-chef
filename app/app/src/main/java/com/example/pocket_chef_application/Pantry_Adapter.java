package com.example.pocket_chef_application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Pantry_Adapter extends RecyclerView.Adapter<Pantry_Adapter.PantryViewHolder> {


    private List<Pantry_Item> list;
    private Context context;

    public Pantry_Adapter(List<Pantry_Item> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.pantry_item,parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.titleTextView.setText(list.get(position).getTitle());
        holder.itemImageView.setImageResource(list.get(position).getImage());

        // click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PantryItem_Activity.class);

                // pass specific card details to activity
                intent.putExtra("Name", list.get(position).getTitle());
                intent.putExtra("Exp_Date", list.get(position).getExp_date());
                intent.putExtra("Amount", list.get(position).getAmount());
                intent.putExtra("Image",list.get(position).getImage());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        ImageView itemImageView;
        CardView cardView;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
            cardView = (CardView) itemView.findViewById(R.id.pantry_item_cardview);
        }
    }
}
