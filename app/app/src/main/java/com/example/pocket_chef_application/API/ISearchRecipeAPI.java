package com.example.pocket_chef_application.API;

import com.example.pocket_chef_application.Model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ISearchRecipeAPI {
    @GET("recipes")
    Call<List<Recipe>> getRecipeList();

    @POST("search")
    @FormUrlEncoded
    Call<List<Recipe>> searchRecipe(@Field("search") String searchQuery);

    @POST("possible")
    @FormUrlEncoded
    Call<List<Recipe>> possibleRecipe(@Field("possible") String searchQuery);
}
