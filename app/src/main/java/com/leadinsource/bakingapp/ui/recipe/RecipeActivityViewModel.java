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
 *
 */

public class RecipeActivityViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> recipes;
    private MutableLiveData<List<Step>> recipeSteps;
    private MediatorLiveData<Boolean> isLastStep;
    private MutableLiveData<Recipe> recipeToDisplay;
    private MutableLiveData<Recipe> currentRecipe;
    private MutableLiveData<Step> currentStep;
    private Repository repo;

    // indices tracking for navigation
    private int currentStepIndex;
    private int lastStepIndex;

    public RecipeActivityViewModel(Application application) {
        super(application);
        // Getting instance of repository
        repo = Repository.getInstance(this.getApplication().getApplicationContext());

        // getting recipes from the repo
        recipes = repo.getRecipes();

        //the following triggers onChange which sets the initial configuration for
        recipeToDisplay = new MutableLiveData<>();
        recipeToDisplay.setValue(null);
        //we need to track the current recipe and step
        currentRecipe = new MutableLiveData<>();
        currentStep = new MutableLiveData<>();
        currentStep.setValue(null);
    }

    public void setRecipeToDisplay(Recipe recipe) {
        recipeToDisplay.postValue(recipe);
        currentRecipe.setValue(recipe);


        setRecipeSteps(recipe.getSteps());
    }

    private void setRecipeSteps(Step[] steps) {
        if(recipeSteps == null) {
            recipeSteps = new MutableLiveData<>();
        }
        recipeSteps.setValue(Arrays.asList(steps));
        lastStepIndex = steps.length-1;
    }

    public void setCurrentStep(Step step) {
        Timber.d("Setting current step %s", step);
        currentStep.postValue(step);
        currentStepIndex = Integer.valueOf(step.getId());
        recipeToDisplay.setValue(null);
    }

    public LiveData<Step> getCurrentStep() {
        return currentStep;
    }

    public void moveToNext() {

        Boolean last = isLastStep.getValue();
        Timber.d("Moving to next, is it last? %s", last);
        if(last!=null && !last) {
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
        if(isLastStep ==null) {
            isLastStep = new MediatorLiveData<>();
            isLastStep.addSource(getCurrentStep(), new Observer<Step>() {
                @Override
                public void onChanged(@Nullable Step step) {
                    if(step==null) return;
                    if(currentStepIndex==lastStepIndex) {
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
        if(currentRecipe==null) {
            currentRecipe = new MutableLiveData<>();
        }

        return currentRecipe;
    }
}