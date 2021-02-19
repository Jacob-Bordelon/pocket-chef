package com.example.pocket_chef_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocket_chef_application.API.ISearchRecipeAPI;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.data.Item;
import com.example.pocket_chef_application.data.LocalDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    private String server_ip = "10.0.2.2";//"10.0.2.2" "54.144.65.217"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Context context = getApplicationContext();

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

        // Lambda handler of fab.
        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // grab all of the info from every components
                EditText a = findViewById(R.id.recipeName);
                EditText b = findViewById(R.id.ingredientsList);
                EditText c = findViewById(R.id.instructions);
                EditText d = findViewById(R.id.prepTime);
                EditText e = findViewById(R.id.cookTime);

                String recipeName = a.getText().toString();
                String ingredientsList = b.getText().toString();
                String instructions = c.getText().toString();
                String prepTime = d.getText().toString();
                String cookTime = e.getText().toString();
                Log.d("Success Save", recipeName);
                Toast.makeText(context, "Recipe Saved", Toast.LENGTH_LONG).show();
                // ....
                // create query to the remote database
                // Creates a dummy JSON array of objects to simulate ingredients

                JSONArray ingredients = getUploadJSONArray();

                // Search for recipes based on the ingredients we have
                jsonPlaceHolderApi.uploadRecipe(recipeName); // => sending the recipe
                jsonPlaceHolderApi.uploadRecipe(ingredientsList); // => sending the ingredient 1
                jsonPlaceHolderApi.uploadRecipe(instructions);
                jsonPlaceHolderApi.uploadRecipe(prepTime);
                jsonPlaceHolderApi.uploadRecipe(cookTime);
            }
        });
    }
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
*/

}
