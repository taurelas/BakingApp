package com.leadinsource.bakingapp.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.repo.Repository;

import java.util.List;

/**
 * Created by Matt on 18/04/2018.
 */

public class MainActivityViewModel extends ViewModel {
    private Repository repo;
    private LiveData<List<Recipe>> recipes;

    public MainActivityViewModel() {
        // Getting instance of repository
        repo = Repository.getInstance();
        recipes = repo.getRecipes();
    }

    LiveData<List<Recipe>> getRecipeNames() {

        return recipes;
    }

}
