package com.leadinsource.bakingapp.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.net.DownloadService;
import com.leadinsource.bakingapp.net.RecipesResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matt on 14/04/2018.
 */

public class Repository {
    private static final String BASE_URL = "http://go.udacity.com/android-baking-app-json/";

    private static Repository instance;
    private MutableLiveData<List<Recipe>> recipes;

    public static Repository getInstance() {
        if(instance==null) {
            instance = new Repository();
        }

        return instance;
    }

    public LiveData<List<Recipe>> getRecipes() {
        if(recipes==null) {
            recipes = new MutableLiveData<>();
            fetchRecipes();
        }

        return recipes;
    }


    private void fetchRecipes() {
        Call<RecipesResponse> call = getDownloadService().downloadRecipes(BASE_URL);
        call.enqueue(new Callback<RecipesResponse>() {
            @Override
            public void onResponse(Call<RecipesResponse> call, Response<RecipesResponse> response) {
                if(!response.isSuccessful()) {
                    try {
                        Log.d("retrofit", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                RecipesResponse decodedResponse = response.body();
                if(decodedResponse==null) {
                    return;
                }
                if(recipes==null) {
                    recipes = new MutableLiveData<>();
                }
                recipes.postValue(decodedResponse.recipeList);

            }

            @Override
            public void onFailure(@NonNull Call<RecipesResponse> call, @NonNull Throwable t) {
                Log.d("retrofit", t.getMessage());
            }
        });
    }

    private DownloadService getDownloadService() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(DownloadService.class);
    }

}
