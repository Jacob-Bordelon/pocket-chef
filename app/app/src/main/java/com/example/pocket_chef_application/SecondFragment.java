package com.example.pocket_chef_application;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pocket_chef_application.API.ISearchRecipeAPI;
import com.example.pocket_chef_application.Model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondFragment extends Fragment {
    // Textview variables
    TextView textView; // -> for displaying the recipes
    TextView statusBar; // -> for displaying connection status
    private static final String TEXT = "text";

    public static SecondFragment newInstance(String text){
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(TEXT,text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Initializes the view and the status bar on create for non null refereance 
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        statusBar = (TextView) view.findViewById(R.id.status_field);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Gets the specific textview from layout 
        textView = view.findViewById(R.id.recipe_view);

        // Check connection with server with UDP. It has to be a thread. Uses the statusBar for updating
        new Thread(new UDPClient(statusBar,3000,"54.144.65.217")).start();
        // ToDo: change to a safe credentials check
        OkHttpClient okHttpClient = UnSafeOkHttpClient.getUnsafeOkHttpClient();
        // Retrofit Client. Creates connection parameters to AWS EC2 Server through port 3000
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://54.144.65.217:3000/")
                .client(okHttpClient) // Checks certification
                .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                .build(); // Build retrofit
        // Initialize interface with retrofit client
        ISearchRecipeAPI jsonPlaceHolderApi = retrofit.create(ISearchRecipeAPI.class);

        // Set action listener for generate button
        view.findViewById(R.id.recipe_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates a dummy JSON array of obejects to simulate ingredients
                JSONObject item1 = new JSONObject();
                JSONObject item2 = new JSONObject();
                JSONObject item3 = new JSONObject();
                JSONObject item4 = new JSONObject();
                try {
                    item1.put("Ingredient","sugar");
                    item2.put("Ingredient","egg");
                    item3.put("Ingredient","chocolate");
                    item4.put("Ingredient","salt");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray ingredients = new JSONArray();

                ingredients.put(item1);
                ingredients.put(item2);
                ingredients.put(item3);
                ingredients.put(item4);

                // Search for recipes based on the ingredients we have
                Call<List<Recipe>> possibleRecipeList = jsonPlaceHolderApi.possibleRecipe(ingredients.toString());
                possibleRecipe(possibleRecipeList);
            }
        });
    }

    // Method that gets fired when we do a REST request for getting the possible recipes 
    private void possibleRecipe(Call<List<Recipe>> possibleRecipes) {
        textView.setText(""); // -> resets the content of the textview
        // send the request and notify callback of its response or if an error occurred talking to the server,
        possibleRecipes.enqueue(new Callback<List<Recipe>>() { 
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) { // -> on response from server
                if (!response.isSuccessful()) { // -> not successful warns user  
                    textView.setText("Code " + response.code()); 
                    return;
                }
                // Parses the payload based on the model: Recipe
                List<Recipe> posts = response.body();
                // traverses through the posts and adds recipe info to the view
                for (Recipe post : posts) {
                    String content = "";
                    content += "Name: " + post.getRecipe() + "\n";
                    content += "Description: " + post.getDescription() + "\n";
                    content += "Instructions: " + post.getInstructions() + "\n";
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
}