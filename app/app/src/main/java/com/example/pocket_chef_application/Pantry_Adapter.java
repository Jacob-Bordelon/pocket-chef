package com.example.pocket_chef_application;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.data.LocalDB;
import com.example.pocket_chef_application.util.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pantry_Adapter extends RecyclerView.Adapter<Pantry_Adapter.PantryViewHolder> {

    private Dialog mDialog;
    private List<Pantry_Item> list;
    private Context context;


    public Pantry_Adapter(List<Pantry_Item> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pantry_item,parent, false);
        mDialog = new Dialog(view.getContext());


        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.titleTextView.setText(list.get(position).getTitle());
        holder.itemImageView.setImageResource(list.get(position).getImage());
    }

    public void ShowPopup(Pantry_Item i){
        TextView closebtn, name, exp_date, amount;
        ImageView img;
        ImageButton  optionsbtn, deletebtn;


        mDialog.setContentView(R.layout.dialog_pantry_item_details);
        name = mDialog.findViewById(R.id.item_name);
        exp_date = mDialog.findViewById(R.id.item_exp);
        amount = mDialog.findViewById(R.id.item_amount);
        img = mDialog.findViewById(R.id.item_image);

        name.setText(i.getTitle());
        exp_date.setText(i.getExp_date());
        amount.setText(Integer.toString(i.getAmount()));


        closebtn = (TextView) mDialog.findViewById(R.id.closebtn);
        optionsbtn = (ImageButton) mDialog.findViewById(R.id.optionsbtn);
        deletebtn = (ImageButton) mDialog.findViewById(R.id.deletebtn);


        closebtn.setOnClickListener(v -> mDialog.dismiss());
        optionsbtn.setOnClickListener(v -> EditOperation(i));
        deletebtn.setOnClickListener(v -> RemoveItem(i));


        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    private void EditOperation(Pantry_Item i){
        mDialog.setContentView(R.layout.dialog_edit_pantry_item_details);
        TextView name = mDialog.findViewById(R.id.item_name);
        name.setText(i.getTitle());

        EditText amount = mDialog.findViewById(R.id.edititem_amount);
        amount.setHint(Integer.toString(i.getAmount()));
        EditText exp = mDialog.findViewById(R.id.edititem_exp);
        exp.setHint(i.getExp_date());

        Button submit = mDialog.findViewById(R.id.submitbtn);
        TextView cancel = mDialog.findViewById(R.id.closebtn);

        cancel.setOnClickListener(n-> mDialog.onBackPressed());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!amount.getText().toString().matches("")){
                    i.setAmount(Integer.parseInt(amount.getText().toString()));
                    i.getItem().amount = Integer.parseInt(amount.getText().toString());
                }

                if(!exp.getText().toString().matches("")){
                    i.setExp_date(exp.getText().toString());
                    i.getItem().exp_date = exp.getText().toString();
                }

                mDialog.dismiss();
            }
        });


    }

    private void RemoveItem(Pantry_Item i){
        int pos = list.indexOf(i);
        list.remove(i);
        LocalDB db = LocalDB.getDBInstance(this.context);
        db.itemDAO().delete(i.getItem());
        mDialog.dismiss();
        this.notifyItemRemoved(pos);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class PantryViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {

        TextView titleTextView;
        ImageView itemImageView;
        CardView cardView;


        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
            cardView = (CardView) itemView.findViewById(R.id.pantry_item_cardview);


            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            ShowPopup(list.get(getAdapterPosition()));

        }
    }
}
