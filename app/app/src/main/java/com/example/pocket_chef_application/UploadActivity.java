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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (record=="None"){
                    Toast.makeText(context, "Measurement can't be blank", Toast.LENGTH_SHORT).show();
                }
                else {
                    JSONObject ingredientObj = new JSONObject();
                    EditText a = findViewById(R.id.ingredient);
                    EditText b = findViewById(R.id.amount);

                    String ingredientName = a.getText().toString();
                    String amount = b.getText().toString();
                    try {
                        ingredientObj.put("Name", ingredientName);
                        ingredientObj.put("Amount", amount);
                        ingredientObj.put("Unit", record);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    a.getText().clear();
                    b.getText().clear();
                    String full = ingredientName+" "+amount+" "+record;
                    //adding new ingredients to the list
                    ingredientsList.put(ingredientObj);
                    //
                    Toast.makeText(context, ingredientObj.toString(), Toast.LENGTH_SHORT).show();
                    measurement.setSelection(11);
                }
            }
        });

        // Lambda handler of fab.
        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // grab all of the info from every components
                EditText a = findViewById(R.id.recipeName);
                EditText b = findViewById(R.id.instructions);
                EditText c = findViewById(R.id.prepTime);
                EditText d = findViewById(R.id.cookTime);

                String recipeName = a.getText().toString();
                String instructions = b.getText().toString();
                String prepTime = c.getText().toString();
                String cookTime = d.getText().toString();
                try{
                    fullRecipe.put("RecipeName",recipeName);
                    fullRecipe.put("Ingredients",ingredientsList);
                    fullRecipe.put("PrepTime",prepTime);
                    fullRecipe.put("CookTime",cookTime);
                    fullRecipe.put("Instructions",instructions);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Success Save", recipeName);
                Toast.makeText(context, "Recipe Saved", Toast.LENGTH_LONG).show();
                Log.i("Full Recipe",fullRecipe.toString());

                // ....
                // create query to the remote database
                // Creates a dummy JSON array of objects to simulate ingredients
/*
                JSONArray ingredients = getUploadJSONArray();
*/
                // Search for recipes based on the ingredients we have
                jsonPlaceHolderApi.uploadRecipe(recipeName); // => sending the recipe
                //
                jsonPlaceHolderApi.uploadRecipe(ingredientsList.toString());
                //
                jsonPlaceHolderApi.uploadRecipe(instructions);
                jsonPlaceHolderApi.uploadRecipe(prepTime);
                jsonPlaceHolderApi.uploadRecipe(cookTime);
            }
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

    /*
 //can improvise
    public JSONArray getUploadJSONArray() {
        JSONArray fullRecipe = new JSONArray();
        JSONObject recipeNameObj = new JSONObject();
        JSONObject prepTimeObj = new JSONObject();
        JSONObject cookTimeObj = new JSONObject();
        JSONObject instructionsObj = new JSONObject();

        try{
            recipeNameObj.put("RecipeName",this.recipeName);

        }

        return jsArray;
    }
}
*/