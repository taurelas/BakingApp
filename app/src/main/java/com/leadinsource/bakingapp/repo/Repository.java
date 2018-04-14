package com.leadinsource.bakingapp.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.net.DownloadService;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The app's data repository
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
        Call<List<Recipe>> call = getDownloadService().downloadRecipes(BASE_URL);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if(!response.isSuccessful()) {
                    try {
                        //noinspection ConstantConditions
                        Log.d("retrofit2.", response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                List<Recipe> decodedResponse = response.body();
                if(decodedResponse==null) {
                    return;
                }
                if(recipes==null) {
                    recipes = new MutableLiveData<>();
                }
                recipes.postValue(decodedResponse);

            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.d("retrofit2.0", t.getMessage());
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
