package com.leadinsource.bakingapp.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leadinsource.bakingapp.NavigationFragment;
import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;
import com.leadinsource.bakingapp.ui.main.MainActivity;

import timber.log.Timber;

/**
 * Created by Matt on 18/04/2018.
 */

public class RecipeActivity extends AppCompatActivity {

    boolean twoPanes;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        RecipeActivityViewModel viewModel = ViewModelProviders.of(this).get(RecipeActivityViewModel.class);

        Intent intent = getIntent();

        if(intent!=null) {
            recipe = intent.getParcelableExtra(MainActivity.EXTRA_RECIPE);
            viewModel.setRecipeToDisplay(recipe);
        }


        View view = findViewById(R.id.step_detail_container);

        if(view!=null) {
            //two panes
            twoPanes = true;
            Timber.d("Two panes");
            StepListFragment stepListFragment = new StepListFragment();
            /* Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.EXTRA_RECIPE, recipe);
            stepListFragment.setArguments(bundle); */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_list_container, stepListFragment)
              /*      .addToBackStack("Recipe") */
                    .commit();

        } else {
            //one pane
            twoPanes = false;
            StepListFragment stepListFragment = new StepListFragment();
            /* Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.EXTRA_RECIPE, recipe);
            stepListFragment.setArguments(bundle); */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_list_container, stepListFragment)
              /*      .addToBackStack("Recipe") */
                    .commit();
        }


        viewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                if(step!=null) {
                    StepDetailFragment stepDetailFragment = new StepDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MainActivity.EXTRA_STEP, step);
                    stepDetailFragment.setArguments(bundle);
                    //we only want 1 step in the back stack
                    getSupportFragmentManager().popBackStackImmediate("Step", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    // if we have two panes
                    if(twoPanes) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, stepDetailFragment)
                            /*    .addToBackStack("Step") */
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_list_container, stepDetailFragment)
                                .addToBackStack("Step")
                                .commit();

                        NavigationFragment navigationFragment = new NavigationFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.bottom_navigation, navigationFragment, "NAV")
                                .commit();
                    }
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
