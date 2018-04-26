package com.leadinsource.bakingapp.ui.recipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;
import com.leadinsource.bakingapp.repo.Repository;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * View Model for Recipe Activity, handles in particular the navigation buttons by providing
 * isFirst(), isLast() & moveToNext()
 */

public class RecipeActivityViewModel extends AndroidViewModel {
    private MediatorLiveData<List<Step>> recipeSteps;
    private MediatorLiveData<Boolean> isLastStep;
    private MediatorLiveData<Recipe> currentRecipe;
    private MutableLiveData<Step> currentStep;
    private MutableLiveData<Integer> currentRecipeId;
    private List<Recipe> recipes;
    private Repository repo;

    // indices tracking for navigation
    private int currentStepIndex;
    private int lastStepIndex;

    public RecipeActivityViewModel(Application application) {
        super(application);
        Timber.d("Getting instance of repository");
        repo = Repository.getInstance(this.getApplication().getApplicationContext());

        repo.getRecipes().observeForever(new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> changedRecipes) {
                if (changedRecipes != null) {
                    recipes = changedRecipes;
                }
            }
        });

        //we need to track the current recipe and step
        currentRecipeId = new MutableLiveData<>();

        recipeSteps = new MediatorLiveData<>();

        currentStep = new MutableLiveData<>();
        currentStep.setValue(null);
    }

    public void setCurrentStep(Step step) {
        Timber.d("Setting current step %s", step);
        currentStep.postValue(step);
        currentStepIndex = Integer.valueOf(step.getId());
        Timber.d("Current step %s", currentStepIndex);

    }

    public LiveData<Step> getCurrentStep() {
        return currentStep;
    }

    public void moveToNext() {
        Boolean last = isLastStep.getValue();
        Timber.d("Moving to next, is it last? %s", last);
        if (last != null && !last) {
            currentStepIndex++;
            currentStep.postValue(recipeSteps.getValue().get(currentStepIndex));
        }
    }

    public void moveToPrevious() {
        if (!isFirst()) {
            currentStepIndex--;
            currentStep.postValue(recipeSteps.getValue().get(currentStepIndex));
        }
    }

    public boolean isFirst() {
        return currentStepIndex == 0;
    }

    @NonNull
    public LiveData<Boolean> isLastStep() {
        if (isLastStep == null) {
            isLastStep = new MediatorLiveData<>();
            isLastStep.addSource(getCurrentStep(), new Observer<Step>() {
                @Override
                public void onChanged(@Nullable Step step) {
                    if (step == null) return;
                    if (currentStepIndex == lastStepIndex) {
                        isLastStep.setValue(true);
                    } else {
                        isLastStep.setValue(false);
                    }
                }
            });
        }

        return isLastStep;
    }

    public LiveData<Recipe> getCurrentRecipe() {
        if (currentRecipe == null) {
            currentRecipe = new MediatorLiveData<>();
            currentRecipe.addSource(currentRecipeId, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer id) {
                    if (id!=null) {
                        Recipe recipe = getRecipeById(id);
                        currentRecipe.postValue(recipe);
                        recipeSteps.postValue(Arrays.asList(recipe.getSteps()));
                        lastStepIndex = recipe.steps.length - 1;
                    }
                }
            });

            Timber.d("Creating new current recipe");
        }
        Timber.d("Providing current recipe");
        return currentRecipe;
    }

    public void setCurrentRecipeId(int recipeId) {
        currentRecipeId.postValue(recipeId);
    }


    // utility method to determine which id holds the value
    private Recipe getRecipeById(int id) {

        for (Recipe recipe : recipes) {
            int idFromString = Integer.valueOf(recipe.getId());
            if (recipe.uid == id || idFromString == id) {
                return recipe;
            }
        }

        return new Recipe();
    }
}