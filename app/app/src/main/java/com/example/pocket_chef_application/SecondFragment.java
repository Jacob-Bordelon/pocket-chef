package com.example.pocket_chef_application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pocket_chef_application.API.ISearchRecipeAPI;
import com.example.pocket_chef_application.Model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondFragment extends Fragment {

    TextView textView;
    TextView statusBar;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        statusBar = (TextView) view.findViewById(R.id.status_field);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.recipe_view);

        // ToDo: change to a safe credentials check
        OkHttpClient okHttpClient = UnSafeOkHttpClient.getUnsafeOkHttpClient();
        // Retrofit Client. Connects to AWS EC2 Server through port 3000
        // ToDo: check if necessary to verify connection with UDP call. Fast as it is
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://192.168.0.6:3000/")
                .client(okHttpClient) // Checks certification
                .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                .build(); // Build retrofit
        // Initialize interface with retrofit client
        ISearchRecipeAPI jsonPlaceHolderApi = retrofit.create(ISearchRecipeAPI.class);

        // Check connection with server with UDP
        new Thread(new UDPClient(statusBar)).start();

        // Set action listener to search for a recipe with EditText input
        view.findViewById(R.id.recipe_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dummy list array for fetching possible recipes
                // Building the JSON Objects
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
                // Building the JSON array
                ingredients.put(item1);
                ingredients.put(item2);
                ingredients.put(item3);
                ingredients.put(item4);

                // Search for possible recipes based on pantry list (dummy: ingredients)
                Call<List<Recipe>> possibleRecipeList = jsonPlaceHolderApi.possibleRecipe(ingredients.toString());
                possibleRecipe(possibleRecipeList);
            }
        });

    }

    private void possibleRecipe(Call<List<Recipe>> possibleRecipes) {

        // clear text to erase previously generated output
        textView.setText("");
        possibleRecipes.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                if (!response.isSuccessful()) {
                    textView.setText("Code " + response.code());
                    return;
                }

                List<Recipe> posts = response.body();

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
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }
}