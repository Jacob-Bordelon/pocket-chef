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

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondFragment extends Fragment {

    TextView textView;
    EditText editText;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        editText = (EditText) view.findViewById(R.id.search_field);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.recipe_view);

        OkHttpClient okHttpClient = UnSafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://54.144.65.217:3000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISearchRecipeAPI jsonPlaceHolderApi = retrofit.create(ISearchRecipeAPI.class);

        // Get all recipes
        Call<List<Recipe>> allRecipesList = jsonPlaceHolderApi.getRecipeList();
        getAllRecipes(allRecipesList);

        view.findViewById(R.id.recipe_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipeName = editText.getText().toString();
                // Search for a recipe by name
                Call<List<Recipe>> singleRecipeList = jsonPlaceHolderApi.searchRecipe(recipeName);
                searchRecipe(singleRecipeList);
            }
        });

    }

    private void searchRecipe(Call<List<Recipe>> allRecipes) {

        allRecipes.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                textView.setText("");
                if (!response.isSuccessful()) {
                    textView.setText("Code " + response.code());
                    return;
                }

                List<Recipe> posts = response.body();

                for (Recipe post : posts) {
                    String content = "";
                    content += "Name: " + post.getRecipe() + "\n";
                    content += "Instructions: " + post.getInstructions() + "\n";
                    content += "Amount: " + post.getAmount() + "\n";
                    content += "Measure: " + post.getMeasure() + "\n";
                    content += "Ingredient: " + post.getIngredient() + "\n";

                    textView.append(content);

                }

            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    private void getAllRecipes(Call<List<Recipe>> allRecipes) {

        allRecipes.enqueue(new Callback<List<Recipe>>() {
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
                    content += "Instructions: " + post.getInstructions() + "\n";
                    content += "Amount: " + post.getAmount() + "\n";
                    content += "Measure: " + post.getMeasure() + "\n";
                    content += "Ingredient: " + post.getIngredient() + "\n";

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