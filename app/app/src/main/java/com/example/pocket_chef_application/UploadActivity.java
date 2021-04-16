package com.example.pocket_chef_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Firebase.SuggestionAdapter;
import com.example.pocket_chef_application.Gen_Recipes.Ingredient;
import com.example.pocket_chef_application.Model.Camera;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.Recipe_utils.Ingredient_Adapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends Dialog
        implements android.view.View.OnClickListener {

    final static String TAG = UploadActivity.class.getSimpleName();
    private TextView cancel, save, upload;
    private EditText name, prep_time, cook_time, desc;
    private LinearLayout ingredients;
    private Spinner diff;
    private ImageView image;
    private Activity activity;

    private ArrayList<Ingredient> ingredientsList;
    private String[] unitsList;




    public UploadActivity(Activity a) {
        super(a);
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload);
        setupViews();
        setupOnClickListeners();
        setupListViews();



    }

    private void setupViews(){
        name = findViewById(R.id.rec_name);
        prep_time = findViewById(R.id.rec_prep);
        cook_time = findViewById(R.id.rec_cook);
        desc = findViewById(R.id.rec_desc);
        ingredients = findViewById(R.id.rec_ingredients);
        image = findViewById(R.id.rec_img);
        diff = findViewById(R.id.rec_difficulty);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.savebtn);
        upload = findViewById(R.id.uploadbtn);

        unitsList= getContext().getResources().getStringArray(R.array.units);
        ingredientsList = new ArrayList<>();



    }

    private void setupOnClickListeners(){
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    private void setupListViews(){
        newIngredient();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void newIngredient(){
        View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_view, null, false);
        EditText amount = (EditText) ingredientView.findViewById(R.id.amount);
        AutoCompleteTextView prompt = (AutoCompleteTextView) ingredientView.findViewById(R.id.prompt);
        Spinner units = (Spinner) ingredientView.findViewById(R.id.units);
        Button add = (Button) ingredientView.findViewById(R.id.button5);

        List<String> keyList = new ArrayList<>();
        SuggestionAdapter adapter = new SuggestionAdapter(getContext(),android.R.layout.simple_dropdown_item_1line,keyList);
        prompt.setAdapter(adapter);
        FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();

        prompt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: "+s.toString());



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, unitsList);
        units.setAdapter(arrayAdapter);

        add.setOnClickListener(v -> {
            if (!amount.getText().toString().matches("") && !prompt.getText().toString().matches("") && !units.getSelectedItem().toString().matches("")) {
                int amnt = Integer.parseInt(amount.getText().toString());
                String name = prompt.getText().toString();
                String unit = units.getSelectedItem().toString();
                ingredientsList.add(new Ingredient(amnt, name, unit));
                add.setOnClickListener(v1 -> { removeIngredient(ingredientView); });
                add.setBackground(getContext().getDrawable(R.drawable.ic_baseline_delete_24));
                newIngredient();
            }else {
                Toast.makeText(getContext(),"Values have been left empty", Toast.LENGTH_SHORT).show();
            }

        });

        ingredients.addView(ingredientView);

    }

    private void removeIngredient(View view){
        ingredients.removeView(view);
    }

    private void grabAllValues(){
        // Basic string arguments
        //TODO - grab all edittext values

        // Complex string arguments
        //TODO - grab all listview items

        // Image
        //TODO - grab image resource
    }

    private void toJsonFormat(){
        //TODO take the aquired values and convert to the json/recipe format
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                dismiss();
                break;
            case R.id.savebtn:
                //TODO - grab all values from each pane
                //TODO - save those values to a json file type structure
                //TODO - convert json file to a pretty print file type
                //TODO - store that file locally on the device
                break;
            case R.id.uploadbtn:
                //TODO - grab all values from each pane
                //TODO - save those values to a json file type structure
                //TODO - send the file to firebase (send the image to storage
                break;
        }

    }
}

