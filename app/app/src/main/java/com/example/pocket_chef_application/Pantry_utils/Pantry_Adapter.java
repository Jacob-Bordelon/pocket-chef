package com.example.pocket_chef_application.Pantry_utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Pantry;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.data.LocalDB;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;

public class Pantry_Adapter extends RecyclerView.Adapter<Pantry_Adapter.PantryViewHolder> {

    private Dialog mDialog;
    private List<Pantry_Item> list;
    private ArrayList<Pantry_Item> arrayList;
    private Context context;
    private final String TAG = Pantry_Adapter.class.getSimpleName();


    public Pantry_Adapter(List<Pantry_Item> list, Context context) {
        this.list = list;
        this.context = context;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(list);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pantry_item,parent, false);
        mDialog = new Dialog(view.getContext());
        return new PantryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void ShowPopup(Pantry_Item i){
        TextView closebtn, name, exp_date, amount, cals, protein_g;
        TextView fat_g, fat_p;
        TextView carbs_g, carbs_p;
        TextView sodium_g, sodium_p;
        TextView chol_g, chol_p;

        ImageView img;
        ImageButton  optionsbtn, deletebtn;

        // Grab views from .xml
        mDialog.setContentView(R.layout.dialog_pantry_item_details);
        name            = mDialog.findViewById(R.id.item_name);
        exp_date        = mDialog.findViewById(R.id.item_exp);
        amount          = mDialog.findViewById(R.id.item_amount);
        img             = mDialog.findViewById(R.id.item_image);
        cals            = mDialog.findViewById(R.id.calories);
        protein_g          = mDialog.findViewById(R.id.protein_grams);

        fat_g           = mDialog.findViewById(R.id.total_fat_grams);
        fat_p           = mDialog.findViewById(R.id.total_fat_perc);

        carbs_g           = mDialog.findViewById(R.id.total_carbs_grams);
        carbs_p           = mDialog.findViewById(R.id.total_carbs_perc);

        sodium_g           = mDialog.findViewById(R.id.sodium_grams);
        sodium_p           = mDialog.findViewById(R.id.sodium_perc);

        chol_g           = mDialog.findViewById(R.id.chol_grams);
        chol_p           = mDialog.findViewById(R.id.chol_perc);

        closebtn        = mDialog.findViewById(R.id.closebtn);
        optionsbtn      = mDialog.findViewById(R.id.optionsbtn);
        deletebtn       = mDialog.findViewById(R.id.deletebtn);

        int total_cals = i.getItem().calories;
        int protein = i.getItem().protein;
        double total_fat = i.getItem().total_fat;
        double sodium = i.getItem().sodium;
        double cholesterol = i.getItem().cholesterol;
        double carbs = i.getItem().carbs;

        // set values and listeners in views
        name.setText(i.getTitle());
        exp_date.setText(i.getExp_date().toString());
        amount.setText(Integer.toString(i.getAmount()));
        cals.setText(Integer.toString(total_cals));
        protein_g.setText(Integer.toString(protein)+"g");

        fat_g.setText((int) total_fat +"g");
        int f_perc = (int) Math.ceil((total_fat*100) / context.getResources().getInteger(R.integer.DV_fat));
        fat_p.setText( f_perc+"%");

        sodium_g.setText((int) sodium +"mg");
        int s_perc = (int) Math.ceil((sodium*100) / context.getResources().getInteger(R.integer.DV_sodium));
        sodium_p.setText(s_perc +"%");

        carbs_g.setText((int) carbs +"g");
        int carb_perc = (int) Math.ceil((carbs / context.getResources().getInteger(R.integer.DV_carbs)));
        carbs_p.setText( carb_perc+"%");

        chol_g.setText((int) cholesterol +"mg");
        int chol_perc = (int) Math.ceil((cholesterol / context.getResources().getInteger(R.integer.DV_chol)));
        chol_p.setText(chol_perc +"%");

        // set image
        if(i.getImageUrl() != null  && !i.getImageUrl().equals("")){
            Picasso.get()
                    .load(i.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(img);
            Log.i(TAG, "Popup: Image loaded into dialog for-"+i.getTitle());
        }
        else{
            Log.i(TAG, "Popup: No image loaded for-"+i.getTitle());
           img.setImageResource(R.drawable.no_image_found);
        }

        closebtn.setOnClickListener(v -> mDialog.dismiss());
        optionsbtn.setOnClickListener(v -> EditOperation(i));
        deletebtn.setOnClickListener(v -> RemoveItem(i));

        // display window
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    private void EditOperation(Pantry_Item i){
        mDialog.setContentView(R.layout.dialog_edit_pantry_item_details);
        TextView name = mDialog.findViewById(R.id.item_name);
        name.setText(i.getTitle());

        ImageView img = mDialog.findViewById(R.id.item_image);
        if(i.getImageUrl() != null && !i.getImageUrl().equals("")){
            Picasso.get()
                    .load(i.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(img);
        }

        EditText amount = mDialog.findViewById(R.id.edititem_amount);
        amount.setHint(Integer.toString(i.getAmount()));
        EditText exp = mDialog.findViewById(R.id.edititem_exp);
        exp.setHint(i.getExp_date().toString());

        Button submit = mDialog.findViewById(R.id.submitbtn);
        TextView cancel = mDialog.findViewById(R.id.closebtn);

        cancel.setOnClickListener(n-> mDialog.onBackPressed());

        submit.setOnClickListener(v -> {
            if(!amount.getText().toString().matches("")){
                i.setAmount(Integer.parseInt(amount.getText().toString()));
                i.getItem().amount = Integer.parseInt(amount.getText().toString());
            }

            if(!exp.getText().toString().matches("")){
                //TODO update exp date
            }

            mDialog.dismiss();
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

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.titleTextView.setText(list.get(position).getTitle());

        if(list.get(position).getImageUrl() != null && !list.get(position).getImageUrl().equals("")){

            Picasso.get()
                    .load(list.get(position).getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.itemImageView);
        }
        else{
            holder.itemImageView.setImageResource(R.drawable.no_image_found);
        }

        LayerDrawable layers = (LayerDrawable) holder.expShape.getBackground();
        RotateDrawable rotate = (RotateDrawable) layers.findDrawableByLayerId(R.id.corner_mark);
        GradientDrawable shape = (GradientDrawable) rotate.getDrawable();
        //int color = setExpFlag(list.get(position));
        shape.setColor(context.getColor(R.color.fresh));
    }

    public class PantryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView expShape;
        ImageView itemImageView;
        CardView cardView;


        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
            expShape = (TextView) itemView.findViewById(R.id.item_exp);

            cardView = (CardView) itemView.findViewById(R.id.pantry_item_cardview);


            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            ShowPopup(list.get(getBindingAdapterPosition()));

        }
    }

    public void filter(String item){
        item = item.toLowerCase();
        list.clear();

        if(item.length() == 0){
            list.addAll(arrayList);
        }else{
            for(Pantry_Item i: arrayList){
                if(i.getTitle().toLowerCase().contains(item)){
                    list.add(i);
                }
            }
        }
        notifyDataSetChanged();

    }

    private int setExpFlag(Pantry_Item item){
        // Get current date and recipe_item expiration date
        Date currentTime = Calendar.getInstance().getTime();
        String itemDate = item.getExp_date().toString();

        // Cast string date to date object
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date item_expDate = sdf.parse(itemDate);

            long diff = item_expDate.getTime() - currentTime.getTime();
            int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);



        }catch (ParseException e){
            e.printStackTrace();
        }


        return 0;
    }
}
