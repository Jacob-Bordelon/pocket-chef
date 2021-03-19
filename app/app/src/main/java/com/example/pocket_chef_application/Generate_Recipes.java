package com.example.pocket_chef_application;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocket_chef_application.API.ISearchRecipeAPI;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.data.DBItem;
import com.example.pocket_chef_application.data.LocalDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Generate_Recipes extends Fragment {
    // Textview variables
    private TextView textView; // -> for displaying the recipes
    private TextView statusBar; // -> for displaying connection status
    private String server_ip = "172.28.72.167";//"10.0.2.2" "54.144.65.217"
    private static final String TEXT = "text";
    private Button recipe_button;
    private ArrayList<RecipeCard> recipeCards = new ArrayList<>();
    private ArrayList<IngredientFlag> ingflags = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView flagrecyclerView;

    public static Generate_Recipes newInstance(String text){
        Generate_Recipes fragment = new Generate_Recipes();
        Bundle args = new Bundle();
        args.putString(TEXT,text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initializes the view and the status bar on create for non null reference
        View view = inflater.inflate(R.layout.generate_recipes, container, false);
        statusBar = (TextView) view.findViewById(R.id.status_field);
        recipe_button = (Button) view.findViewById(R.id.recipe_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Create Recycler view

        recyclerView = view.findViewById(R.id.gen_recipe_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new Adapter(recipeCards, view.getContext()));


        // Check connection with server with UDP. It has to be a thread. Uses the statusBar for updating
        new Thread(new UDPClient(statusBar,3000,server_ip)).start();

        OkHttpClient okHttpClient = UnSafeOkHttpClient.getUnsafeOkHttpClient();
        // Retrofit Client. Creates connection parameters to AWS EC2 Server through port 3000
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://"+server_ip+":3000/")
                .client(okHttpClient) // Checks certification
                .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                .build(); // Build retrofit
        // Initialize interface with retrofit client
        ISearchRecipeAPI jsonPlaceHolderApi = retrofit.create(ISearchRecipeAPI.class);

        // Set action listener for generate button
        recipe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates a dummy JSON array of obejcts to simulate ingredients
                JSONArray ingredients = getPantryJSONArray();

                // Search for recipes based on the ingredients we have
                Call<List<Recipe>> possibleRecipeList = jsonPlaceHolderApi.possibleRecipe(ingredients.toString());
                possibleRecipe(possibleRecipeList);
            }
        });
    }

    // Method that gets fired when we do a REST request for getting the possible recipes 
    private void possibleRecipe(Call<List<Recipe>> possibleRecipes) {
        //recipeCards.clear();
        //recyclerView.getAdapter().notifyDataSetChanged();
        // send the request and notify callback of its response or if an error occurred talking to the server,
        possibleRecipes.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) { // -> on response from server
                if (!response.isSuccessful()) { // -> not successful warns user  
                    if (response.code() == 500) {
                        Toast.makeText(getContext(),"No recipes were found",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(),"Code"+response.code(),Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                // Parses the payload based on the model: Recipe
                List<Recipe> posts = response.body();

                // traverses through the posts and adds recipe info to the view
                for (Recipe post : posts) {
                    System.out.println(post.getRecipe());
                    String content = "";
                    content += "Name: " + post.getRecipe() + "\n";
                    content += "Description: " + post.getDescription() + "\n";
                    content += "Instructions: " + post.getInstructions() + "\n";
                    content += "Ingredients:\n";
                    ArrayList<String> ingredients = post.getIngredients();
                    JSONArray jsonArray = null;
                    JSONObject jsonObject = null;

                    try {
                        jsonArray = new JSONArray(post.getIngredients().toString());
                        for(int i=0;i<jsonArray.length();i++) {
                            jsonObject = (JSONObject) jsonArray.get(i);
                            content += jsonObject.getString("Ingredient") +" "+ jsonObject.getString("Amount")+" "+ jsonObject.getString("Measure") + "\n";
                        }
                    } catch(JSONException | NullPointerException ignored) {}


                    content += "#############################" + "\n";

                    textView.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) { // -> if failure, sets message
                Log.e("ConnectionFailure", t.getMessage());
                textView.setText(t.getMessage());
            }
        });
    }

    public JSONArray getPantryJSONArray() {
        LocalDB db = LocalDB.getDBInstance(this.getContext());
        List<DBItem> items = db.itemDAO().getAllItems();
        JSONArray jsArray = new JSONArray();

        for (DBItem item : items) {
            JSONObject jsonObject = new JSONObject();
            try { jsonObject.put("Ingredient",item.item_Name);}
            catch (org.json.JSONException jerr) {}
            jsArray.put(jsonObject);
        }

        return jsArray;
    }
}