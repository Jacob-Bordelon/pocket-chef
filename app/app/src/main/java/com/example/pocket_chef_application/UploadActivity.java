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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String server_ip = "10.0.2.2";//"10.0.2.2" "54.144.65.217"
    private List ingredientsList = new ArrayList();
    private String record;
    private Integer numberOfIngredients = 0;


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
                    EditText a = findViewById(R.id.ingredient);
                    EditText b = findViewById(R.id.amount);

                    String ingredient = a.getText().toString();
                    a.getText().clear();
                    String amount = b.getText().toString();
                    b.getText().clear();
                    String full = ingredient+" "+amount+" "+record;
                    ingredientsList.add(full);
                    Toast.makeText(context, full, Toast.LENGTH_SHORT).show();
                    numberOfIngredients++;
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
                Log.d("Success Save", recipeName);
                Toast.makeText(context, "Recipe Saved", Toast.LENGTH_LONG).show();

                // ....
                // create query to the remote database
                // Creates a dummy JSON array of objects to simulate ingredients
/*
                JSONArray ingredients = getUploadJSONArray();
*/
                // Search for recipes based on the ingredients we have
                jsonPlaceHolderApi.uploadRecipe(recipeName); // => sending the recipe
                for (int i=0;i<=numberOfIngredients;i++){
                    jsonPlaceHolderApi.uploadRecipe(ingredientsList.get(i).toString());
                }
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
    public JSONArray getUploadJSONArray() {
      List<Item> items = ingredientsList
        JSONArray jsArray = new JSONArray();

        for (Item item : items) {
            JSONObject jsonObject = new JSONObject();
            try { jsonObject.put("Ingredient",item.item_Name);}
            catch (org.json.JSONException jerr) {}
            jsArray.put(jsonObject);
        }

        return jsArray;
    }
}
*/