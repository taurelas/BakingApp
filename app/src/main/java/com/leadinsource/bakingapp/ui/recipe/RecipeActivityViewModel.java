package com.leadinsource.bakingapp.ui.recipe;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;
import com.leadinsource.bakingapp.repo.Repository;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Matt on 14/04/2018.
 */

public class RecipeActivityViewModel extends ViewModel {
    private LiveData<List<Recipe>> recipes;
    private MutableLiveData<List<Step>> recipeSteps;
    private MediatorLiveData<Boolean> isLastStep;
    private MutableLiveData<Recipe> recipeToDisplay;
    private MutableLiveData<Recipe> currentRecipe;
    private MutableLiveData<Step> currentStep;
    private Repository repo;

    // indices tracking for navigation
    private int currentStepIndex;
    private int currentRecipeIndex;
    private LiveData<Integer> lastRecipeIndex;
    private int lastStepIndex;

    public RecipeActivityViewModel() {
        // Getting instance of repository
        repo = Repository.getInstance();

        // getting recipes from the repo
        recipes = repo.getRecipes();

        // this is needed for navigation, to check if it's the last recipe

        lastRecipeIndex = Transformations.map(recipes, new Function<List<Recipe>, Integer>() {
            @Override
            public Integer apply(List<Recipe> input) {
                return input.size()-1;
            }
        }) ;

        //the following triggers onChange which sets the initial configuration for
        recipeToDisplay = new MutableLiveData<>();
        recipeToDisplay.setValue(null);
        //we need to track the current recipe and step
        currentRecipe = new MutableLiveData<>();
        currentStep = new MutableLiveData<>();
        currentStep.setValue(null);
    }

    LiveData<List<Recipe>> getRecipeNames() {

        return recipes;
    }

    public void setRecipeToDisplay(Recipe recipe) {
        recipeToDisplay.postValue(recipe);
        currentRecipe.setValue(recipe);
        currentRecipeIndex = recipes.getValue().indexOf(recipe);

        setRecipeSteps(recipe.getSteps());

    }

    private void setRecipeSteps(Step[] steps) {
        if(recipeSteps == null) {
            recipeSteps = new MutableLiveData<>();
        }
        recipeSteps.setValue(Arrays.asList(steps));
        lastStepIndex = steps.length-1;
    }


    public LiveData<Recipe> getRecipeToDisplay() {

        return recipeToDisplay;
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
        if(isFirst()) {
            // nothing for now
        } else {
            currentStepIndex--;
            currentStep.postValue(recipeSteps.getValue().get(currentStepIndex));
        }
    }

    public boolean isFirst() {
        if(currentStepIndex==0) {
            return true;
        } else {
            return false;
        }
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
