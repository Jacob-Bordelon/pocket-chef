package com.example.pocket_chef_application.Pantry_utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Pantry;
import com.example.pocket_chef_application.R;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemsRecyclerView extends RecyclerView.Adapter<ItemsRecyclerView.ItemViewHolder>{
    private List<Food> foodList;
    private ArrayList<Food> arrayList;
    private Context context;
    private Dialog mDialog;
    private final static String TAG = ItemsRecyclerView.class.getSimpleName();
    private PantryTextWatcher tw;
    private EditText amount, exp;
    private Button submit;
    private TextView name;
    private ImageView img;


    public ItemsRecyclerView(List<Food> foodList, Context context){
        this.context = context;
        this.foodList = foodList;

        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(foodList);


    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.suggestion_layout, parent, false);
        mDialog = new Dialog(view.getContext());
        setupDialog();
        return new ItemViewHolder(view);
    }


    public void setupDialog(){
        mDialog.setContentView(R.layout.dialog_add_new_item);
        amount = mDialog.findViewById(R.id.edititem_amount);
        exp = mDialog.findViewById(R.id.edititem_exp);
        name = mDialog.findViewById(R.id.item_name);
        submit = mDialog.findViewById(R.id.submitbtn);
        img = mDialog.findViewById(R.id.item_image);
        TextView cancel = mDialog.findViewById(R.id.closebtn);

        cancel.setOnClickListener(n-> mDialog.onBackPressed());
        tw = new PantryTextWatcher(exp);
        exp.addTextChangedListener(tw);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.name.setText(foodList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Food food = foodList.get(getAdapterPosition());
            EditOperation(food);


        }
    }

    private void EditOperation(Food i){
        name.setText(i.getName());
        if(i.getImage() != null && !i.getImage().equals("")){
            Picasso.get()
                    .load(i.getImage())
                    .fit()
                    .centerCrop()
                    .into(img);
        }

        submit.setOnClickListener(v -> {

            Pantry.AddItem(i, exp.getText().toString(), Integer.parseInt(amount.getText().toString()));
            mDialog.dismiss();

        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();


    }
}
