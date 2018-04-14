package com.leadinsource.bakingapp.net;

import com.leadinsource.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Retrofit Service for downloading list of Recipes
 */

public interface DownloadService {
    @GET
    Call<List<Recipe>> downloadRecipes(@Url String url);
}
