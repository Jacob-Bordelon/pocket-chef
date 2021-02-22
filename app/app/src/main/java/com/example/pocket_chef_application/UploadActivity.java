package com.example.pocket_chef_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pocket_chef_application.API.ISearchRecipeAPI;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.data.Item;
import com.example.pocket_chef_application.data.LocalDB;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String server_ip = "10.0.2.2";//"10.0.2.2" "54.144.65.217"
    private JSONArray ingredientsList = new JSONArray();
    private JSONObject fullRecipe = new JSONObject();
    private String record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Context context = getApplicationContext();

        Spinner measurement = findViewById(R.id.measure);
        ArrayAdapter<CharSequence> measurementAdapter = ArrayAdapter.createFromResource(this, R.array.measurement, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurement.setAdapter(measurementAdapter);
        measurement.setSelection(11);
        measurement.setOnItemSelectedListener(this);


        // ToDo: change to a safe credentials check
        OkHttpClient okHttpClient = UnSafeOkHttpClient.getUnsafeOkHttpClient();

        // ToDo: create a retrofit client class
        // Retrofit Client. Creates connection parameters to AWS EC2 Server through port 3000
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://"+server_ip+":3000/")
                .client(okHttpClient) // Checks certification
                .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                .build(); // Build retrofit
        // Initialize interface with retrofit client
        ISearchRecipeAPI jsonPlaceHolderApi = retrofit.create(ISearchRecipeAPI.class);

        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
            if (record=="None"){
                Toast.makeText(context, "Measurement can't be blank", Toast.LENGTH_SHORT).show();
            }
            else {
                JSONObject ingredientObj = new JSONObject();
                EditText ingredientName = findViewById(R.id.ingredient);
                EditText amount = findViewById(R.id.amount);

                try {
                    ingredientObj.put("Name", ingredientName.getText().toString());
                    ingredientObj.put("Amount", amount.getText().toString());
                    ingredientObj.put("Unit", record);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ingredientName.getText().clear();
                amount.getText().clear();

                //adding new ingredients to the list
                ingredientsList.put(ingredientObj);
                //
                Toast.makeText(context, ingredientObj.toString(), Toast.LENGTH_SHORT).show();
                measurement.setSelection(11);
            }
        });

        // Lambda handler of fab.
        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(v -> {
            // grab all of the info from every components
            EditText recipeName = findViewById(R.id.recipeName);
            EditText instructions = findViewById(R.id.instructions);
            EditText prepTime = findViewById(R.id.prepTime);
            EditText cookTime = findViewById(R.id.cookTime);

            try{
                fullRecipe.put("RecipeName", recipeName.getText().toString());
                fullRecipe.put("Ingredients", ingredientsList);
                fullRecipe.put("PrepTime", prepTime.getText().toString());
                fullRecipe.put("CookTime", cookTime.getText().toString());
                fullRecipe.put("Instructions", instructions.getText().toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(context, "Recipe Saved", Toast.LENGTH_LONG).show();
            Log.i("Full Recipe",fullRecipe.toString());

            jsonPlaceHolderApi.uploadRecipe(fullRecipe.toString());
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                record = "Tablespoon";
                break;
            case 1:
                record = "Cup";
                break;
            case 2:
                record = "Teaspoon";
                break;
            case 3:
                record = "Liter";
                break;
            case 4:
                record = "Gram";
                break;
            case 5:
                record = "Ounce";
                break;
            case 6:
                record = "Pound";
                break;
            case 7:
                record = "Pint";
                break;
            case 8:
                record = "Quart";
                break;
            case 9:
                record = "Gallon";
                break;
            case 10:
                record = "Fluid Ounce";
                break;
            case 11:
                record = "None";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }}
