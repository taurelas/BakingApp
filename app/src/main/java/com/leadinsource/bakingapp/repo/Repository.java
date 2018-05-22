package com.leadinsource.bakingapp.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.leadinsource.bakingapp.db.AppDatabase;
import com.leadinsource.bakingapp.db.DbUtil;
import com.leadinsource.bakingapp.db.RecipeDao;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.RecipeWithData;
import com.leadinsource.bakingapp.net.DownloadService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * The app's data repository
 */

public class Repository {
    private static final String BASE_URL = "http://go.udacity.com/android-baking-app-json/";
    private MutableLiveData<Boolean> idle;
    private static Repository instance;
    private MediatorLiveData<List<Recipe>> recipes;
    private LiveData<List<RecipeWithData>> recipesWithData;
    private RecipeDao recipeDao;
    private AppDatabase db;

    private Repository(Context context) {
        initDb(context);
    }

    public static Repository getInstance(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }

        return instance;
    }

    private void initDb(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "data").build();
        recipeDao = db.recipeDao();
        setIdle(false);
        recipesWithData = recipeDao.getAllLiveData();
    }

    public LiveData<List<Recipe>> getRecipes() {
        if(recipes==null) {
            recipes = new MediatorLiveData<>();
            recipes.addSource(recipesWithData, new Observer<List<RecipeWithData>>() {
                @Override
                public void onChanged(@Nullable List<RecipeWithData> recipesWithData) {
                    if(recipesWithData==null || recipesWithData.size()==0) {
                        fetchRecipes();
                    } else {
                        ArrayList<Recipe> recipesList = new ArrayList<>();

                        for (RecipeWithData recipeWithData : recipesWithData) {
                            recipeWithData.passDataDown();
                            recipesList.add(recipeWithData.getRecipe());
                        }

                        recipes.setValue(recipesList);
                        setIdle(true);
                    }
                }
            });
        }

        return recipes;
    }


    private void fetchRecipes() {
        Timber.d("Fetching recipes");
        Call<List<Recipe>> call = getDownloadService().downloadRecipes(BASE_URL);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (!response.isSuccessful()) {
                    try {
                        //noinspection ConstantConditions
                        Timber.d(response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                List<Recipe> decodedResponse = response.body();
                if (decodedResponse == null) {
                    return;
                }

                recipes.postValue(decodedResponse);
                Timber.d("Inserting data into db");
                DbUtil.insertRecipes(recipeDao, decodedResponse);

                // for Espresso Idling Resources
                idle.setValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Timber.d(t.getMessage());
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

    public LiveData<Boolean> getIdleness() {
        if (idle == null) {
            idle = new MutableLiveData<>();
            idle.setValue(true);
        }

        return idle;
    }

    public void setIdle(boolean value) {
        if (idle == null) {
            idle = new MutableLiveData<>();
        }
        idle.setValue(value);
    }

    public void finish() {
        db.close();
    }

}
