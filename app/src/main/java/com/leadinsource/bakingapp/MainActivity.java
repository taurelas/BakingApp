package com.leadinsource.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity  {

    public static final String EXTRA_RECIPE = "extra_recipe";
    public static final String EXTRA_STEP = "extra_step";
    private NavigationFragment navigationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d("App start");
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        RecipeListFragment recipeListFragment = new RecipeListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, recipeListFragment)
                .commit();

        viewModel.getRecipeToDisplay().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if(recipe!=null) {
                    Timber.d("Recipe is not null");
                    setTitle(recipe.getName());
                    StepListFragment stepListFragment = new StepListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MainActivity.EXTRA_RECIPE, recipe);
                    stepListFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, stepListFragment)
                            .addToBackStack("Recipe")
                            .commit();

                }
            }
        });

        viewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {

                if(step!=null) {
                    Timber.d("Step changed to %s", step.getShortDescription());
                    StepDetailFragment stepDetailFragment = new StepDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MainActivity.EXTRA_STEP, step);
                    stepDetailFragment.setArguments(bundle);
                    //we only want 1 step in the back stack
                    getSupportFragmentManager().popBackStackImmediate("Step", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, stepDetailFragment)
                            .addToBackStack("Step")
                            .commit();
                    navigationFragment = new NavigationFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.bottom_navigation, navigationFragment, "NAV")
                            .commit();
                }
            }
        });




    }

    @Override
    public void onBackPressed() {
        Timber.d("On back pressed");
        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("NAV");

        if(fragment!=null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            Timber.d("Fragment is null");
        }


    }
}
