package com.example.pocket_chef_application.Pantry_utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.R;

public class Searchbar_Adapter extends RecyclerView.Adapter<Searchbar_Adapter.SearchbarViewHolder> {


    @NonNull
    @Override
    public SearchbarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchbarViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SearchbarViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView expShape;
        ImageView itemImageView;
        CardView cardView;


        public SearchbarViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
            expShape = (TextView) itemView.findViewById(R.id.item_exp);

            cardView = (CardView) itemView.findViewById(R.id.pantry_item_cardview);



        }


    }
}
