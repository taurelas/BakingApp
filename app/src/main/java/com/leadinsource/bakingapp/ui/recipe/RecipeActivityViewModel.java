package com.leadinsource.bakingapp.ui.recipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;
import com.leadinsource.bakingapp.repo.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.leadinsource.bakingapp.ui.recipe.RecipeActivity.EXTRA_RECIPE_ID;

/**
 * View Model for Recipe Activity, handles in particular the navigation buttons by providing
 * isFirst(), isLast() & moveToNext()
 */

public class RecipeActivityViewModel extends AndroidViewModel {
    private static final String DISPLAY_INGREDIENTS_KEY = "display_ingredients_key";
    private static final String CURRENT_STEP_KEY = "current_step_key";
    private static final String CURRENT_TIME_KEY = "current_time_key";
    private static final String IS_PLAYING_KEY = "is_playing_key";

    private MediatorLiveData<List<Step>> recipeSteps;
    private MediatorLiveData<Boolean> isLastStep;
    private long time = 0;
    private boolean isPlaying = true;
    private MediatorLiveData<Boolean> isFirstStep;
    private MediatorLiveData<Recipe> currentRecipe = new MediatorLiveData<>();
    private MediatorLiveData<Step> currentStep = new MediatorLiveData<>();
    @NonNull
    private MediatorLiveData<List<Ingredient>> displayedIngredients = new MediatorLiveData<>();
    private MutableLiveData<Boolean> displayIngredients = new MutableLiveData<>();
    private MutableLiveData<Integer> currentRecipeId;
    private MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private Repository repo;

    // indices tracking for navigation
    private int currentStepIndex;
    private MutableLiveData<Integer> currentStepIndexLiveData = new MutableLiveData<>();
    private int lastStepIndex;
    private MediatorLiveData<Integer> lastStepIndexLiveData = new MediatorLiveData<>();

    public RecipeActivityViewModel(Application application) {
        super(application);
        Timber.d("Getting instance of repository");
        repo = Repository.getInstance(this.getApplication().getApplicationContext());

        repo.getRecipes().observeForever(new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> changedRecipes) {
                if (changedRecipes != null) {
                    recipes.postValue(changedRecipes);
                }
            }
        });

        lastStepIndexLiveData.addSource(recipeSteps, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                if(steps!=null) {
                    lastStepIndexLiveData.postValue(steps.size()-1);
                }
            }
        });

        //we need to track the current recipe and step
        currentRecipeId = new MutableLiveData<>();
        displayIngredients.setValue(false);
        recipeSteps = new MediatorLiveData<>();

        currentStep.setValue(null);

        currentStep.addSource(currentStepIndexLiveData, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer!=null && recipeSteps!=null && recipeSteps.getValue()!=null) {
                    currentStep.postValue(recipeSteps.getValue().get(integer));
                }
            }
        });

        currentStep.addSource(recipeSteps, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                if(steps!=null && currentStepIndexLiveData!=null && currentStepIndexLiveData.getValue()!=null) {
                    currentStep.postValue(recipeSteps.getValue().get(currentStepIndexLiveData.getValue()));
                }
            }
        });

        currentRecipe.addSource(currentRecipeId, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                if (id != null && recipes != null && recipes.getValue() !=null) {
                    Timber.d("Recipe id has changed");
                    Recipe recipe = getRecipeById(id, recipes.getValue());
                    currentRecipe.postValue(recipe);
                    recipeSteps.postValue(Arrays.asList(recipe.getSteps()));
                    lastStepIndex = recipe.steps.length - 1;
                }
            }
        });

        currentRecipe.addSource(recipes, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null && currentRecipeId != null && currentRecipeId.getValue()!=null) {
                    Timber.d("List of recipes has changed");
                    Recipe recipe = getRecipeById(currentRecipeId.getValue(), recipes);
                    currentRecipe.postValue(recipe);
                    Timber.d("Recipe steps: %s, recipe: %s", recipeSteps, recipe);
                    recipeSteps.postValue(Arrays.asList(recipe.getSteps()));
                    lastStepIndex = recipe.steps.length - 1;
                }
            }
        });

        //each time recipe changes, ingredients change

        currentRecipe.observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    Timber.d("Current recipe has changed so changing ingredients to size %s", recipe.ingredients.length);
                    ingredients = Arrays.asList(recipe.ingredients);
                    if(displayIngredients.getValue()!= null && displayIngredients.getValue()) {
                        displayedIngredients.postValue(ingredients);
                    }
                } else {
                    Timber.d("But the recipe is null so it's not great");
                }
            }
        });


        displayedIngredients.addSource(displayIngredients, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean displayIngredients) {
                Timber.d("Display ingredients boolean changed");
                if (displayIngredients != null) {
                    if (displayIngredients) {
                        Timber.d("Posting ingredients");
                        displayedIngredients.postValue(ingredients);
                    } else {
                        Timber.d("Posting null");
                        displayedIngredients.postValue(null);
                    }
                } else {
                    Timber.d("Display ingredients is null");
                }
            }
        });

    }

    // utility method to determine which id holds the value
    private Recipe getRecipeById(@Nullable Integer id, @Nullable List<Recipe> recipes) {

        if (recipes!=null && id!=null) {
            for (Recipe recipe : recipes) {
                int idFromString = Integer.valueOf(recipe.getId());
                if (recipe.uid == id || idFromString == id) {
                    return recipe;
                }
            }
        }

        return new Recipe();
    }

    public void showIngredients() {
        displayIngredients.postValue(true);
    }

    private void hideIngredients() {
        displayIngredients.postValue(false);
    }

    public void setCurrentStep(Step step) {
        Timber.d("ROTATION Setting current step %s", step);
        hideIngredients();
        currentStep.postValue(step);
        currentStepIndex = Integer.valueOf(step.getId());
        Timber.d("ROTATION Current step %s", currentStepIndex);

    }

    public LiveData<Step> getCurrentStep() {
        return currentStep;
    }

    public void moveToNext() {
        Boolean last = isLastStep.getValue();
        Timber.d("Moving to next, is it last? %s", last);
        resetTime();
        resetPlayingStatus();
        isFirstStep.postValue(false);
        if (isDisplayingIngredients()) {
            hideIngredients();
            currentStepIndex = 0;
            currentStep.postValue(recipeSteps.getValue().get(currentStepIndex));
        } else {
            if (last != null && !last) {
                currentStepIndex++;
                currentStep.postValue(recipeSteps.getValue().get(currentStepIndex));
            }
        }
    }

    public void moveToPrevious() {
        resetTime();
        resetPlayingStatus();
        if (!isFirst()) {
            if (currentStepIndex == 0) {
                showIngredients();
                isFirstStep.postValue(true);
            } else {
                currentStepIndex--;
                isFirstStep.postValue(false);
                currentStep.postValue(recipeSteps.getValue().get(currentStepIndex));
            }
        }
    }

    private boolean isFirst() {

        return isDisplayingIngredients() || !hasIngredients() && currentStepIndex == 0;
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

    @NonNull
    public LiveData<Boolean> isFirstStep() {
        if (isFirstStep == null) {
            isFirstStep = new MediatorLiveData<>();
            isFirstStep.addSource(getCurrentStep(), new Observer<Step>() {
                @Override
                public void onChanged(@Nullable Step step) {
                    if (step == null) {
                        isFirstStep.postValue(isDisplayingIngredients() || !hasIngredients() && currentStepIndex == 0);
                    } else {
                        isFirstStep.postValue(false);
                    }
                }
            });
        }

        return isFirstStep;
    }

    public LiveData<Recipe> getCurrentRecipe() {

        return currentRecipe;
    }

    public void setCurrentRecipeId(int recipeId) {
        currentRecipeId.postValue(recipeId);
    }

    private boolean isDisplayingIngredients() {
        if (displayIngredients == null) {
            return false;
        }
        if (displayIngredients.getValue() == null) {
            return false;
        }

        return displayIngredients.getValue();
    }

    private boolean hasIngredients() {
        if (currentRecipe == null) {
            return false;
        }

        Recipe recipe = currentRecipe.getValue();

        return recipe != null
                && recipe.getIngredients() != null
                && recipe.getIngredients().length >= 1;

    }

    public LiveData<List<Ingredient>> displayIngredients() {
        return displayedIngredients;
    }


    // saving and restoring state
    public void restoreState(Bundle savedInstanceState) {
        Timber.d("restoring state of the ViewModel");
        int recipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID);
        setCurrentRecipeId(recipeId);
        if (savedInstanceState.containsKey(DISPLAY_INGREDIENTS_KEY)) {
            boolean display = savedInstanceState.getBoolean(DISPLAY_INGREDIENTS_KEY, false);
            if (display) {
                showIngredients();
                if(ingredients!=null) {
                    Timber.d("Displaying ingredients: %s", ingredients.toString());
                } else {
                    Timber.d("Displaying ingredients: which are null");
                }

            } else {
                hideIngredients();
            }
        } else {
            hideIngredients();
        }

        if(savedInstanceState.containsKey(CURRENT_STEP_KEY)) {
            int index = savedInstanceState.getInt(CURRENT_STEP_KEY, -1);
            if(index>-1) {
                currentStepIndexLiveData.postValue(index);
                currentStepIndex = index;
            }
        }

        if(savedInstanceState.containsKey(CURRENT_TIME_KEY)) {
            time = savedInstanceState.getLong(CURRENT_TIME_KEY, 0);
        }

        if(savedInstanceState.containsKey(IS_PLAYING_KEY)) {
            isPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY, true);
        }

    }

    public Bundle saveState(Bundle outState) {
        if (isDisplayingIngredients()) {
            outState.putBoolean(DISPLAY_INGREDIENTS_KEY, true);
        }
        if(currentStep!=null && currentStep.getValue()!=null) {
            outState.putInt(CURRENT_STEP_KEY, currentStepIndex);
        }

        if(time!=0L) {
            outState.putLong(CURRENT_TIME_KEY, time);
        }

        if(!isPlaying) {
            outState.putBoolean(IS_PLAYING_KEY, false);
        }

        return outState;
    }

    public void setCurrentTime(long time) {
        this.time = time;
    }

    public void resetTime() {
        this.time = 0L;
    }

    public long getTime() {
        return time;
    }

    public void setPlayingStatus(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean getPlayingStatus() {
        return isPlaying;
    }

    public void resetPlayingStatus() {
        isPlaying = true;
    }
}