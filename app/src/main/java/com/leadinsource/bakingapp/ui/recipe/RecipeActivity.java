package com.leadinsource.bakingapp.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;
import com.leadinsource.bakingapp.widget.ListRemoteViewsFactory;

import java.util.List;

import timber.log.Timber;

/**
 * Activity displaying steps and details for the particular recipe.
 */

public class RecipeActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.leadinsource.bakingapp.ui.recipe.recipe_id";
    public static final int INVALID_RECIPE_ID = -1;
    public static final String NAVIGATION_TAG = "com.leadinsource.bakingapp.ui.recipe.navigation_tag";
    private static final String STEP_DETAIL_KEY = "com.leadinsource.bakingapp.ui.recipe.step_key";
    public static final String STEP_TAG = "com.leadinsource.bakingapp.ui.recipe.step_tag";
    private static final String STEP_LIST_KEY = "com.leadinsource.bakingapp.ui.recipe.step_list_key";

    /**
     * indicates whether the screen displays two panes or one
     */
    boolean twoPanes;

    private IngredientsFragment ingredientsFragment;
    private StepListFragment stepListFragment;
    private StepDetailFragment stepDetailFragment;
    private int recipeId;
    private NavigationFragment navigationFragment;
    private RecipeActivityViewModel viewModel;
    private boolean restoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        viewModel = ViewModelProviders.of(this).get(RecipeActivityViewModel.class);

        if (savedInstanceState != null) {
            // restoring list fragment and its state
            stepListFragment = (StepListFragment) getSupportFragmentManager().getFragment(savedInstanceState, STEP_LIST_KEY);
            recipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID, INVALID_RECIPE_ID);
            stepDetailFragment = (StepDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, STEP_DETAIL_KEY);
            restoring = true;
        } else {
            Timber.d("ROTATION SavedInstanceState is null");
            restoring = false;
            viewModel.resetTime();
            Intent intent = getIntent();
            // finish if no intent, the activity without is useless
            if (intent == null) {
                Toast.makeText(this, "Not sure what your intention is here", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // getting recipeId from intent
                recipeId = intent.getIntExtra(ListRemoteViewsFactory.EXTRA_RECIPE_ID, -1);
            }


        }

        //  finish if invalid recipeId
        if (recipeId <= INVALID_RECIPE_ID) {
            Toast.makeText(this, "Unknown recipe", Toast.LENGTH_SHORT).show();
            finish();
        }

        viewModel.setCurrentRecipeId(recipeId);

        // if step_detail_container exists, it must be two panes
        View view = findViewById(R.id.step_detail_container);
        twoPanes = (view != null);

        // adding list of steps to a layout, this happens ALWAYS
        if (stepListFragment == null) {
            Timber.d("ROTATION Creating new StepListFragment");
            stepListFragment = new StepListFragment();
        }

        //adding list of steps to the container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_list_container, stepListFragment)
                .commit();

        // observing recipe to add title when required
        viewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    Timber.d("ROTATION Recipe changed so setting tht title");
                    setTitle(recipe.getName());
                }
            }
        });

        // now the rest reacts to the changes in viewmodel,
        // if displayingredients holds null, does nothing
        // but if there is a list, display that list and create navigation if necesssary
        // created NavigationFragments starts observing viewmodel too

        viewModel.displayIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> display) {
                if (display != null) {
                    stepListFragment = null;
                    if (ingredientsFragment == null) {
                        ingredientsFragment = new IngredientsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArray(IngredientsFragment.EXTRA_INGREDIENTS, display.toArray(new Ingredient[display.size()]));
                        ingredientsFragment.setArguments(bundle);
                    }

                    // if we have two panes
                    if (twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, ingredientsFragment)
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_list_container, ingredientsFragment)
                                .commit();

                        if (navigationFragment == null) {
                            navigationFragment = new NavigationFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.bottom_navigation, navigationFragment, NAVIGATION_TAG)
                                    .commit();
                        }
                    }
                }
            }
        });

        // if getCurrentSteps holds null, does nothing
        // but if there is a step, display that stepDetailFragment and create navigation if necesssary
        // created NavigationFragments starts observing viewmodel too

        viewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                Timber.d("ROTATION New step detected");
                if (step != null) {
                    Timber.d("ROTATION The step is not null");
                    stepListFragment = null;
                    //stepDetailFragment = (StepDetailFragment) getSupportFragmentManager().findFragmentByTag(STEP_TAG);
                    if (stepDetailFragment != null) {
                        Timber.d("ROTATION StepDetailFragment is not null");
                    } else {
                        Timber.d("ROTATION Creating new stepDetailFragment");
                        stepDetailFragment = new StepDetailFragment();
                    }


                     /* With two panes, we want step details inside a container but we don't want it
                        in the backstack.

                        With one pane, we want step details inside a container but we do want it in
                        the backstack since it covers the whole screen.
                        With one pane, we also need navigation which should not affect the backstack */
                    if (twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, stepDetailFragment, STEP_TAG)
                                .commit();
                    } else {
                        Timber.d("StepdetailFragment id: %s", stepDetailFragment.getId());

                        getSupportFragmentManager().beginTransaction()
                                .remove(stepDetailFragment)
                                .commit();
                        getSupportFragmentManager().executePendingTransactions();

                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.step_list_container, stepDetailFragment)
                                .commit();
                        getSupportFragmentManager().executePendingTransactions();

                        if (navigationFragment == null) {
                            navigationFragment = new NavigationFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.bottom_navigation, navigationFragment, NAVIGATION_TAG)
                                    .commit();

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Timber.d("On save instance state by activity");
        super.onSaveInstanceState(outState);
        // here we go, we need to save the state of the activity so the viewmodel can restore it
        outState.putInt(EXTRA_RECIPE_ID, recipeId);

        outState = viewModel.saveState(outState);

        if (stepListFragment == null) {
            Timber.d("Saving instance state: List is null");
        } else {
            Timber.d("Saving instance state: List is not null!");
            if (stepListFragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState, STEP_LIST_KEY, stepListFragment);
            }
        }

        if (stepDetailFragment == null) {
            Timber.d("Step detail instance state: fragment is null");
        } else {
            Timber.d("Step detail instance state: fragment is not null");
            if (stepDetailFragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState, STEP_DETAIL_KEY, stepDetailFragment);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewModel.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Timber.d("On back pressed");

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(NAVIGATION_TAG);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
            navigationFragment = null;
        } else {
            Timber.d("Fragment is null");
        }
    }
}