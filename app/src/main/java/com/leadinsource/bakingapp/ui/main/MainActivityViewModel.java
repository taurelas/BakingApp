package com.leadinsource.bakingapp.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.repo.Repository;

import java.util.List;

/**
 * ViewModel for MainActivity
 */

public class MainActivityViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> recipes;
    private final Repository repo;

    public MainActivityViewModel(Application application) {
        super(application);
        // Getting instance of repository
        repo = Repository.getInstance(this.getApplication().getApplicationContext());
        recipes = repo.getRecipes();
    }

    LiveData<List<Recipe>> getRecipeNames() {

        return recipes;
    }

    LiveData<Boolean> getIdleness() {
        return repo.getIdleness();
    }
}
