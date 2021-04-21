package com.example.pocket_chef_application.Firebase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.Pantry;
import com.example.pocket_chef_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class FirebaseFoodDatabase_Helper {
    private static final String TAG = "FirebaseFoodDatabase_Helper";
    public FirebaseDatabase mDatabase;
    public static String DEFAULT_PAGE_INDEX = "325871";
    private static DatabaseReference mReference;
    private Context context;
    private RecyclerView recyclerView;
    private List<Food> foodList = new ArrayList<>();
    public String gotoNextPage = "";
    public FoodItemView adapter;
    private int limitAmount = 10;
    private ValueEventListener listener;

    public static String key;



    public interface Data{
        void RetrievedData(List<Food> foods, String nextPage);
    }

    public interface Container{
        void returnData(List<Food> foods);
    }

    public FirebaseFoodDatabase_Helper() {

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("food");
        listener=voidListener();

    }

    public void removeListener(){
        mReference.removeEventListener(listener);
    }

    public ValueEventListener voidListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }


    public void searchFor(String queryText, final Container container){
        mReference.removeEventListener(listener);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    Food food = keyNode.getValue(Food.class);
                    foodList.add(food);
                }
                container.returnData(foodList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReference.orderByChild("fullName")
                .startAt(queryText)
                .endAt(queryText+"\uf8ff")
                .addListenerForSingleValueEvent(listener);

    }

    public void paginate(String i, final Data data){
        mReference.removeEventListener(listener);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                String nextpage = "";
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    Food food = keyNode.getValue(Food.class);
                    foodList.add(food);
                    nextpage = keyNode.getKey();
                }

                data.RetrievedData(foodList, nextpage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReference
                .orderByKey()
                .startAt(i)
                .limitToFirst(limitAmount)
                .addListenerForSingleValueEvent(listener);
    }

    public void defaultPage(){
        paginate(DEFAULT_PAGE_INDEX, (foods, nextPage) -> {
            adapter.updateList(foods);
            gotoNextPage = nextPage;
        });
    }

    public void nextPage(){
        paginate(gotoNextPage, (foods, nextPage) -> {
            adapter.updateList(foods);
            gotoNextPage = nextPage;
        });
    }

    // Custom Firebase Specific Recycler View Class
    public void setConfig(RecyclerView recyclerView, Context context){
        this.context = context;
        this.recyclerView = recyclerView;
        adapter = new FoodItemView();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    public int getAdapterSize(){
        return adapter.items.size();
    }

    public void clearAdapterItems (){
       adapter.clearList();
    }

    public void restoreAdapterItems(){
        adapter.restoreList();
    }


    public class FoodItemView extends RecyclerView.Adapter<FoodItemView.FoodItemHolder>{
        List<Food> items = new ArrayList<>();
        List<Food> backup = new ArrayList<>();

        public FoodItemView(List<Food> items) {
            this.items = items;

        }

        public FoodItemView() {
        }

        public void updateList(List<Food> foods){
            Set<Integer> itemsIDs = items.stream()
                    .map(Food::getFdcId)
                    .collect(toSet());

            foods.stream()
                    .filter(food -> !itemsIDs.contains(food.getFdcId()))
                    .forEach(items::add);

            notifyDataSetChanged();
        }

        public void clearList(){
            items.clear();
            notifyDataSetChanged();
        }

        public void restoreList(){
            items.addAll(backup);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FoodItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FoodItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FoodItemHolder holder, int position) {
            holder.name.setText(items.get(position).getName());

        }

        @Override
        public int getItemCount() {
            return items.size();
        }



        class FoodItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView name;

            public FoodItemHolder(ViewGroup parent){
                super(LayoutInflater.from(context).inflate(R.layout.suggestion_layout, parent, false));

                // Grab views by idea from item.xml using itemView.findViewById instead of view.findViewById
                name = (TextView) itemView.findViewById(R.id.item_name);
                itemView.setOnClickListener(this);

            }

            private void showDialog(Food food) {
                Dialog mDialog = new Dialog(context);
                mDialog.setContentView(R.layout.dialog_add_new_item);

                TextView name = mDialog.findViewById(R.id.item_name);
                TextView closebtn = mDialog.findViewById(R.id.closebtn);
                TextView exp_label = mDialog.findViewById(R.id.exp_label);
                EditText amount = mDialog.findViewById(R.id.edititem_amount);
                //DatePicker expdate = mDialog.findViewById(R.id.edititem_exp);
                TextView submit = mDialog.findViewById(R.id.submitbtn);
                TextView exp_preview = mDialog.findViewById(R.id.exp_preview);
                ImageButton calendar_btn = mDialog.findViewById(R.id.calendar_btn);
                ConstraintLayout otherItems = mDialog.findViewById(R.id.otheritems_layout);

                Calendar mCalender = Calendar.getInstance();
                int year = mCalender.get(Calendar.YEAR);
                int month = mCalender.get(Calendar.MONTH);
                int dayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);

                exp_label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_edit_calendar_24, 0, 0, 0);

                DatePickerDialog expdate = new DatePickerDialog(context, (view, year1, month1, dayOfMonth1) -> {
                    String date = month1+"/"+dayOfMonth1+"/"+year1;
                    exp_preview.setText(date);
                    }, year, month, dayOfMonth);



                ImageView img = mDialog.findViewById(R.id.item_image);
                if(food.getImage() != null && !food.getImage().equals("")){
                    Picasso.get()
                            .load(food.getImage())
                            .fit()
                            .centerCrop()
                            .into(img);
                }

                exp_label.setOnClickListener(v -> expdate.show());

                submit.setOnClickListener(v -> {
                    DatePicker picker = expdate.getDatePicker();
                    String experation = picker.getMonth()+"/"+picker.getDayOfMonth()+"/"+picker.getYear();
                    Log.d(TAG, "showDialog: "+experation);
                    Pantry.AddItem(food, experation, Integer.parseInt(amount.getText().toString()));
                    Toast.makeText(context,"Item added to Pantry", Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                });

                closebtn.setOnClickListener(v->mDialog.dismiss());
                name.setText(food.getName());

                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();
            }

            @Override
            public void onClick(View v) {
                showDialog(items.get(getBindingAdapterPosition()));
            }
        }


    }


}
