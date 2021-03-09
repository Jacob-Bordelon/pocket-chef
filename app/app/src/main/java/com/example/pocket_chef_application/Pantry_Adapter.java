package com.example.pocket_chef_application;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Pantry_Adapter extends RecyclerView.Adapter<Pantry_Adapter.PantryViewHolder> {

    Dialog mDialog;
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
        mDialog = new Dialog(view.getContext());
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
                ShowPopup(v, list.get(position));
            }
        });

    }

    public void ShowPopup(View v, Pantry_Item i){
        TextView closebtn, name, exp_date, amount;
        Button editButton;
        ImageView img;


        mDialog.setContentView(R.layout.activity_pantry_item_);
        name = mDialog.findViewById(R.id.item_name);
        exp_date = mDialog.findViewById(R.id.item_exp);
        amount = mDialog.findViewById(R.id.item_amount);
        img = mDialog.findViewById(R.id.item_image);

        name.setText(i.getTitle());
        exp_date.setText(i.getExp_date());
        amount.setText(Integer.toString(i.getAmount()));
        img.setImageResource(i.getImage());

        closebtn = (TextView) mDialog.findViewById(R.id.closebtn);
        editButton = (Button) mDialog.findViewById(R.id.editbtn);

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        


        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
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
