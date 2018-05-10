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

import com.leadinsource.bakingapp.NavigationFragment;
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

    private static final String EXTRA_RECIPE_ID = "com.leadinsource.bakingapp.ui.recipe.recipe_id";
    public static final int INVALID_RECIPE_ID = -1;
    /**
     * indicates whether the screen displays two panes or one
     */
    boolean twoPanes;

    private IngredientsFragment ingredientsFragment;
    private StepListFragment stepListFragment;
    private StepDetailFragment stepDetailFragment;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate: Amount of stuff in the backstack: %s", getSupportFragmentManager().getBackStackEntryCount());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        final RecipeActivityViewModel viewModel = ViewModelProviders.of(this).get(RecipeActivityViewModel.class);

        // restoring fragments if needed, the fields remain null if not restored
        // also restoring recipeId
        if(savedInstanceState!=null) {
            Timber.d("Restoring fragments");
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, "Step");
            if(fragment instanceof IngredientsFragment) {
                ingredientsFragment = (IngredientsFragment) fragment;
            } else if(fragment instanceof StepDetailFragment) {
                stepDetailFragment = (StepDetailFragment) fragment;
            }

            stepListFragment = (StepListFragment)getSupportFragmentManager().getFragment(savedInstanceState, "StepList");

            recipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID, INVALID_RECIPE_ID);

        } else {
            Timber.d("Creating fresh fragments");
            Intent intent = getIntent();
            // finish if no intent, the activity without is useless
            if (intent == null) {
                Toast.makeText(this, "Not sure what your intention is here", Toast.LENGTH_SHORT).show();
                finish();
            }

            assert intent != null;
            // getting recipeId from intent
            recipeId = intent.getIntExtra(ListRemoteViewsFactory.EXTRA_RECIPE_ID, -1);
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

        // adding list of steps to a layout
        if(stepListFragment==null) {
            stepListFragment = new StepListFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_list_container, stepListFragment)
                .commit();

        // adding title when required
        viewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    Timber.d("Recipe changed");
                    setTitle(recipe.getName());
                }
            }
        });

        viewModel.displayIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> display) {
                Timber.d("Displaying ingredients: Amount of stuff in the backstack: %s", getSupportFragmentManager().getBackStackEntryCount());
                if (display != null) {
                    Timber.d("Displaying ingredients");
                    if(ingredientsFragment==null) {
                        ingredientsFragment = new IngredientsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArray(IngredientsFragment.EXTRA_INGREDIENTS, display.toArray(new Ingredient[display.size()]));
                        ingredientsFragment.setArguments(bundle);
                    }


                    //we only want 1 step in the back stack
                    // so what, we remove all the steps?
                   /* if(ingredientsFragment.isAdded()) {
                        getSupportFragmentManager().popBackStackImmediate("Step", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }*/


                    // if we have two panes
                    if (twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, ingredientsFragment)
                            /*    .addToBackStack("Step") */
                                .commit();
                    } else {
                        Timber.d("Adding to backstack ingredients fragment");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_list_container, ingredientsFragment)
                                .addToBackStack("Step")
                                .commit();

                        NavigationFragment navigationFragment = new NavigationFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.bottom_navigation, navigationFragment, "NAV")
                                .commit();
                        Timber.d("Amount of steps in the backstack: %s", getSupportFragmentManager().getBackStackEntryCount());
                    }
                } else {
                    Timber.d("Ingredients is null");
                }
            }
        });


        viewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                Timber.d("Step changed");
                if (step != null) {
                    if(stepDetailFragment==null) {
                        stepDetailFragment = new StepDetailFragment();
                    }

                  /*  if(stepDetailFragment.isAdded()) {
                        getSupportFragmentManager().popBackStackImmediate("Step", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }*/

                    //we only want 1 step in the back stack

                    /*
                        With two panes, we want step details inside a container but we don't want it
                        in the backstack.

                        With one pane, we want step dteails inside a container but we do want it in
                        the backstack since it covers the whole screen.
                        With one pane, we also need navigation which should not affect the backstack
                     */
                    if (twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, stepDetailFragment)
                            /*    .addToBackStack("Step") */
                                .commit();
                    } else {
                        Timber.d("Adding step %s to backstack a step", step.getId());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_list_container, stepDetailFragment)
                                .addToBackStack("Step")
                                .commit();
                        NavigationFragment navigationFragment = new NavigationFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.bottom_navigation, navigationFragment, "NAV")
                                .commit();

                        Timber.d("Amount of steps in the backstack: %s", getSupportFragmentManager().getBackStackEntryCount());
                    }
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Timber.d("On save instance state by activity");
        super.onSaveInstanceState(outState);

        outState.putInt(EXTRA_RECIPE_ID, recipeId);

        if(ingredientsFragment==null) {
            Timber.d("Saving instance state: ingredients is null");
        } else {
            getSupportFragmentManager().putFragment(outState, "Step", ingredientsFragment);
        }
        if(stepListFragment==null) {
            Timber.d("Saving instance state: List is null");
        } else {
            Timber.d("Saving instance state: List is not null!");
            getSupportFragmentManager().putFragment(outState, "StepList", stepListFragment);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Timber.d("On restore instance state");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Timber.d("On back pressed");

        for(int i=0;i<getSupportFragmentManager().getBackStackEntryCount();i++) {
            Timber.d("Stack entry: %s", getSupportFragmentManager().getBackStackEntryAt(i));
        }


        Fragment fragment = getSupportFragmentManager().findFragmentByTag("NAV");

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            Timber.d("Fragment is null");
        }
    }
}