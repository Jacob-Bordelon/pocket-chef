package com.example.pocket_chef_application.GroceryList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.GLItem;
import com.example.pocket_chef_application.data.LocalDB;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.CustomViewHolder>  {
    private static final String TAG = CustomListAdapter.class.getSimpleName();
    private Context context;
    private List<GroceryItem> itemList;
    public static ArrayList<GroceryItem> backups;
    private boolean edit;

    public CustomListAdapter(Context context, List<GroceryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.edit = false;
    }

    public ArrayList<GroceryItem> difference(){
        ArrayList<GroceryItem> result = backups;
        result.removeAll(itemList);
        return result;
    }



    public void closeEdit(){
        edit=false;
        notifyDataSetChanged();
    }

    public void cancelEdit(){
        itemList.clear();
        itemList.addAll(backups);
        edit = false;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item,parent, false);
        backups = new ArrayList<>();
        return new CustomViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        /*if(edit){
            holder.edit_title.setText(null);
            holder.edit_amount.setText(null);
            holder.image.setImageResource(R.drawable.ic_baseline_delete_24);
            holder.image.setOnClickListener(v -> {
                itemList.remove(position);
                notifyDataSetChanged();
            });
            holder.edit_title.setVisibility(View.VISIBLE);
            holder.edit_amount.setVisibility(View.VISIBLE);
            holder.edit_title.setHint(itemList.get(position).getName());
            holder.edit_amount.setHint(Integer.toString(itemList.get(position).getAmount()));
            holder.title.setVisibility(View.GONE);
            holder.amount.setVisibility(View.GONE);

        }
        else{
            holder.image.setImageResource(R.drawable.ic_baseline_circle_24);
            holder.image.setOnClickListener(null);
            holder.title.setVisibility(View.VISIBLE);
            holder.amount.setVisibility(View.VISIBLE);
            holder.title.setText(itemList.get(position).getName());
            holder.amount.setText(Integer.toString(itemList.get(position).getAmount()));
            holder.edit_title.setVisibility(View.GONE);
            holder.edit_amount.setVisibility(View.GONE);
        }*/
        holder.title.setText(itemList.get(position).getName());
        holder.amount.setText(Integer.toString(itemList.get(position).getAmount()));
    }




    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener{
        TextView title, amount;
        View fade_over;
        ImageView image;
        EditText edit_title, edit_amount;
        boolean edited = false;
        boolean isClicked;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            image = itemView.findViewById(R.id.imageView2);
            fade_over = itemView.findViewById(R.id.fade_over);
            edit_title = itemView.findViewById(R.id.edit_title);
            edit_amount = itemView.findViewById(R.id.edit_amount);



            isClicked = false;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View v) {
            if(!edit) {
                if(!isClicked){
                    itemView.setBackgroundResource(R.drawable.strikethrough);
                    fade_over.setBackgroundColor(ResourcesCompat.getColor(itemView.getResources(), R.color.grabbed, null));
                }else {
                    itemView.setBackgroundResource(0);
                    fade_over.setBackgroundColor(ResourcesCompat.getColor(itemView.getResources(), R.color.white, null));
                }
                isClicked = !isClicked;
            }
        }

        private void showDialog(){
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.edit_list_item);
            EditText edit_amount = dialog.findViewById(R.id.edit_amount);
            EditText edit_title = dialog.findViewById(R.id.edit_title);
            TextView cancel_btn = dialog.findViewById(R.id.cancel_btn);
            TextView submit_btn = dialog.findViewById(R.id.submit_btn);
            TextView remove_btn = dialog.findViewById(R.id.remove_btn);

            edit_amount.setHint(Integer.toString(itemList.get(getBindingAdapterPosition()).getAmount()));
            edit_title.setHint(itemList.get(getBindingAdapterPosition()).getName());
            cancel_btn.setOnClickListener(v -> dialog.dismiss());

            submit_btn.setOnClickListener(v -> {
                GroceryItem item = itemList.get(getBindingAdapterPosition());
                if(!edit_amount.getText().toString().matches("")){
                    item.setAmount(Integer.parseInt(edit_amount.getText().toString()));
                }

                if(!edit_title.getText().toString().matches("")){
                    item.setName(edit_title.getText().toString());
                }

                GroceryList.db.itemDAO().updateGLItem(item.getItem());
                notifyItemChanged(getBindingAdapterPosition());
                dialog.dismiss();

            });

            remove_btn.setOnClickListener(v -> {
                GroceryItem item = itemList.get(getBindingAdapterPosition());
                GroceryList.db.itemDAO().removeGLItem(item.getItem());
                itemList.remove(item);
                notifyDataSetChanged();
                dialog.dismiss();
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        @Override
        public boolean onLongClick(View view) {

            showDialog();
            return true;
        }
    }


}
