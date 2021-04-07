package com.example.pocket_chef_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pocket_chef_application.API.ISearchRecipeAPI;
import com.example.pocket_chef_application.Gen_Recipes.Ingredient;
import com.example.pocket_chef_application.Model.Camera;
import com.example.pocket_chef_application.Model.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static ConstraintLayout layout;
    private static FragmentManager manager;
    private String record;
    final static String TAG = UploadActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Context context = getApplicationContext();
        layout = findViewById(R.id.upload_fragment);

        Spinner measurement = findViewById(R.id.measure);
        ArrayAdapter<CharSequence> measurementAdapter = ArrayAdapter.createFromResource(this, R.array.measurement, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurement.setAdapter(measurementAdapter);
        measurement.setSelection(11);
        measurement.setOnItemSelectedListener(this);

        EditText ingredientName = findViewById(R.id.ingredient);
        EditText amount = findViewById(R.id.amount);
        EditText recipeName = findViewById(R.id.recipeName);
        EditText descriptions = findViewById(R.id.description);
        EditText instructions = findViewById(R.id.instructions);
        EditText prepTime = findViewById(R.id.prepTime);
        EditText cookTime = findViewById(R.id.cookTime);
        HashMap<String, Ingredient> ingredientsList = new HashMap<>();



        Button cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(v -> {
            testUpload();
        });

        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
            if (record.equals("None")){
                Toast.makeText(context, "Measurement can't be blank", Toast.LENGTH_SHORT).show();
            }
            else {

                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredientName.getText().toString());
                ingredient.setAmount(Integer.parseInt(amount.getText().toString()));
                ingredient.setMeasurement(record);

                ingredientsList.put("325871", ingredient);
                ingredientName.getText().clear();
                amount.getText().clear();
                measurement.setSelection(11);



            }
        });


        // Lambda handler of fab.
        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(v -> {

            boolean valuesHere = true;

            // check if any values are empty
            if (recipeName.getText().toString().equals("")){
                Toast.makeText(context, "Missing Recipe Name", Toast.LENGTH_SHORT).show();
                valuesHere = false;
            }

            if (ingredientsList.size() ==0){
                Toast.makeText(context, "Missing Ingredients", Toast.LENGTH_SHORT).show();
                valuesHere = false;
            }

            if (instructions.getText().toString().equals("")){
                Toast.makeText(context, "Missing Instructions", Toast.LENGTH_SHORT).show();
                valuesHere = false;
            }

            if(valuesHere){
                Recipe recipe = new Recipe();
                recipe.setTitle(recipeName.getText().toString());
                recipe.setDescription(descriptions.getText().toString());
                recipe.setPrep_time(Integer.parseInt(prepTime.getText().toString()));
                recipe.setCook_time(Integer.parseInt(cookTime.getText().toString()));
                recipe.setIngredients(ingredientsList);

                HashMap<String, String> instruct = new HashMap<>();
                String[] steps = instructions.getText().toString().split("\n");

                for(int i = 0; i < steps.length; i++){
                    instruct.put("step"+i,steps[i]);
                }
                recipe.setInstructions(instruct);
                recipe.setAuthor("jacob bordelon");
                recipe.setId(10001010);
                recipe.setImage("null");
                recipe.setDifficulty("easy");


                uploadRecipe(recipe);
                Toast.makeText(context, "Recipe Saved", Toast.LENGTH_LONG).show();
            }

            });
    }

    private void testUpload(){
        Recipe recipe = new Recipe();
        recipe.setTitle("pancakes");
        recipe.setDescription("Fluffy delicous pancakes");
        recipe.setAuthor("jacob bordelon");
        recipe.setImage("null");
        recipe.setId(100010);

        String[] steps = {"mix the ingredients together", "pour batter on hot plate", "flip after 1 minute", "serve immediately"};
        HashMap<String, String> instruct = new HashMap<>();
        for(int i = 0; i < steps.length; i++){
            instruct.put("step"+i,steps[i]);
        }

        recipe.setInstructions(instruct);

        HashMap<String, Ingredient> ingredientsList = new HashMap<>();
        Ingredient ingredient = new Ingredient();
        ingredient.setName("pancake miz");
        ingredient.setAmount(2);
        ingredient.setMeasurement("Cup");

        ingredientsList.put("325871", ingredient);
        ingredientsList.put("325871", ingredient);
        ingredientsList.put("325871", ingredient);


        recipe.setIngredients(ingredientsList);

        recipe.setRating(3);
        recipe.setDifficulty("easy");
        Log.d(TAG, "testUpload: ");


        uploadRecipe(recipe);

    }

    private void uploadRecipe(Recipe recipe) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("recipeBook/"+recipe.getTitle());
        mDatabase.child("author").setValue(recipe.getAuthor());
        mDatabase.child("prep_time").setValue(recipe.getPrep_time());
        mDatabase.child("cook_time").setValue(recipe.getCook_time());
        mDatabase.child("difficulty").setValue(recipe.getDifficulty());
        mDatabase.child("id").setValue(recipe.getId());
        mDatabase.child("image").setValue(recipe.getImage());
        mDatabase.child("rating").setValue(recipe.getRating());
        mDatabase.child("description").setValue(recipe.getDescription());
        mDatabase.child("ingredients").setValue(recipe.getIngredients());
        mDatabase.child("instructions").setValue(recipe.getInstructions());
        mDatabase.child("serving_size").setValue("1");
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

