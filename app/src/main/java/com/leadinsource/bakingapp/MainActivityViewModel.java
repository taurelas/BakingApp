package com.leadinsource.bakingapp;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.repo.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 14/04/2018.
 */

public class MainActivityViewModel extends ViewModel {
    private LiveData<List<Recipe>> recipes;
    private Repository repo;

    public MainActivityViewModel() {
        repo = Repository.getInstance();
        recipes = repo.getRecipes();
    }


    public LiveData<List<Recipe>> getRecipeNames() {

        return recipes;
    }
}
