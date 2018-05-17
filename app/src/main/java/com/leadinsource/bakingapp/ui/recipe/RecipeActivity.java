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
    public static final String NAVIGATION_KEY = "com.leadinsource.bakingapp.ui.recipe.navigation_key";
    private static final String STEP_KEY = "com.leadinsource.bakingapp.ui.recipe.step_key";
    private static final String DISPLAY_INGREDIENTS = "display_ingredient_key";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("============== onCreate ============== ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        viewModel = ViewModelProviders.of(this).get(RecipeActivityViewModel.class);

        // restoring fragments if needed, the fields remain null if not restored
        // also restoring recipeId
        if (savedInstanceState != null) {
            //viewModel.restoreState(savedInstanceState);  // this would be ok
            Timber.d("Restoring fragments");

            /*Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, STEP_KEY);
            if (fragment instanceof IngredientsFragment) {
                ingredientsFragment = (IngredientsFragment) fragment;
            } else if (fragment instanceof StepDetailFragment) {
                stepDetailFragment = (StepDetailFragment) fragment;
            }

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, NAVIGATION_KEY);
            if(fragment!=null && fragment instanceof NavigationFragment) {
                navigationFragment = (NavigationFragment) fragment;
            }*/

            // this is the only fragment we are restoring from the original state, the rest is going
            // to be recreated anew

            Timber.d("Restoring list of recipe steps and the state of recyclerview also");
            stepListFragment = (StepListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "StepList");

            recipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID, INVALID_RECIPE_ID);
            //viewModel.restoreState(savedInstanceState);
// ----------------- lets try and avoid the top save for viewmodel.restoreState
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

        // adding list of steps to a layout, this happens ALWAYS
        if (stepListFragment == null) {
            Timber.d("Creating new StepListFragment");
            stepListFragment = new StepListFragment();
        }

        //adding list of steps to the container
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

        // now the rest reacts to the changes in viewmodel,
        // if displayingredients holds null, does nothing
        // but if there is a list, display that list and create navigation if necesssary
        // created NavigationFragments starts observing viewmodel too

        viewModel.displayIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> display) {
                if (display != null) {
                    Timber.d("Displaying ingredients with size %s", display.size());
                    stepListFragment = null;
                    if (ingredientsFragment == null) {
                        Timber.d("Creating ingredients fragment");
                        ingredientsFragment = new IngredientsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArray(IngredientsFragment.EXTRA_INGREDIENTS, display.toArray(new Ingredient[display.size()]));
                        ingredientsFragment.setArguments(bundle);
                    } else {
                        Timber.d("Fragment already exists");
                    }

                    // if we have two panes
                    if (twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, ingredientsFragment)
                                .commit();
                    } else {
                        Timber.d("Adding to backstack ingredients fragment");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_list_container, ingredientsFragment)
                                .commit();

                        if (navigationFragment == null) {
                            navigationFragment = new NavigationFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.bottom_navigation, navigationFragment, NAVIGATION_TAG)
                                    .commit();
                            Timber.d("Amount of steps in the backstack: %s", getSupportFragmentManager().getBackStackEntryCount());
                        }
                    }
                } else {
                    Timber.d("Ingredients is null");
                }
            }
        });

        // if getCurrentSteps holds null, does nothing
        // but if there is a step, display that stepDetailFragment and create navigation if necesssary
        // created NavigationFragments starts observing viewmodel too

        viewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                Timber.d("Step changed, displaying step");
                if (step != null) {
                    stepListFragment = null;
                    Timber.d("Step is not null so creating a stepDetailFragment fragment and replacing");
                    stepDetailFragment = new StepDetailFragment();

                    /*
                        With two panes, we want step details inside a container but we don't want it
                        in the backstack.

                        With one pane, we want step details inside a container but we do want it in
                        the backstack since it covers the whole screen.
                        With one pane, we also need navigation which should not affect the backstack
                     */
                    if (twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, stepDetailFragment)
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_list_container, stepDetailFragment)
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Timber.d("On save instance state by activity");
        super.onSaveInstanceState(outState);
        // here we go, we need to save the state of the activity so the viewmodel can restore it
        outState.putInt(EXTRA_RECIPE_ID, recipeId);

        outState = viewModel.saveState(outState);

        // now we add fragments and we want to avoid it

        /*if (ingredientsFragment == null) {
            Timber.d("Saving instance state: ingredients is null");
        } else {
            if(ingredientsFragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState, STEP_KEY, ingredientsFragment);
                outState.putBoolean(DISPLAY_INGREDIENTS, true);
            }
        } */

        // the only fragment we are saving

        if (stepListFragment == null) {
            Timber.d("Saving instance state: List is null");
        } else {
            Timber.d("Saving instance state: List is not null!");
            if(stepListFragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState, "StepList", stepListFragment);
            }
        }
    /*
        if(navigationFragment == null) {
            Timber.d("Navigation instance state: fragment is null");
        } else {
            Timber.d("Navigation instance state: fragment is not null");
            if(navigationFragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState, NAVIGATION_KEY, navigationFragment);
            }
        }

        if(stepDetailFragment == null) {
            Timber.d("Step detail instance state: fragment is null");
        } else {
            Timber.d("Step detail instance state: fragment is not null");
            if(stepDetailFragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState, STEP_KEY, stepDetailFragment);
            }
        }*/
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Timber.d("On restore instance state");
        super.onRestoreInstanceState(savedInstanceState);
        viewModel.restoreState(savedInstanceState);
        // here is slightly different scenario because the state of the activity is different - viewmodel is gone
        // but in reality the only thing different is viewmodel's state, it should not affect the activity






        /*if(ingredientsFragment!=null) {
            // if we have two panes
            if (twoPanes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, ingredientsFragment)
                        .commit();
            } else {
                Timber.d("Adding to backstack ingredients fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_list_container, ingredientsFragment)
                        .commit();

                if (navigationFragment == null) {
                    navigationFragment = new NavigationFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.bottom_navigation, navigationFragment, NAVIGATION_TAG)
                            .commit();
                    Timber.d("Amount of steps in the backstack: %s", getSupportFragmentManager().getBackStackEntryCount());
                }
            }
        }
        if(stepDetailFragment!=null) {
            if (twoPanes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, stepDetailFragment)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_list_container, stepDetailFragment)
                        .commit();
                if (navigationFragment == null) {
                    navigationFragment = new NavigationFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.bottom_navigation, navigationFragment, NAVIGATION_TAG)
                            .commit();

                }
            }
        }*/

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