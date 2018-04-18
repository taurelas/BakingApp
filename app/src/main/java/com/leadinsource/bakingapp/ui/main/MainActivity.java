package com.leadinsource.bakingapp.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leadinsource.bakingapp.NavigationFragment;
import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.ui.recipe.RecipeActivity;
import com.leadinsource.bakingapp.model.Recipe;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainListAdapter.Callback {

    public static final String EXTRA_RECIPE = "extra_recipe";
    public static final String EXTRA_STEP = "extra_step";
    private NavigationFragment navigationFragment;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d("App start");
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // ItemDecoration code as per https://stackoverflow.com/a/29168276/3886459

        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(
                16, getResources().getInteger(R.integer.main_list_columns)
        ));
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, getResources().getInteger(R.integer.main_list_columns));
        recyclerView.setLayoutManager(lm);

        viewModel.getRecipeNames().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeNames) {
                recyclerView.setAdapter(new MainListAdapter(MainActivity.this, recipeNames));
            }
        });


/*



        /*viewModel.getRecipeToDisplay().observe(this, new Observer<Recipe>() {
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


                    View view = findViewById(R.id.item_detail_container);
                    if(view!=null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, stepDetailFragment)
                                .addToBackStack("Step")
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, stepDetailFragment)
                                .addToBackStack("Step")
                                .commit();
                    }

                    navigationFragment = new NavigationFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.bottom_navigation, navigationFragment, "NAV")
                            .commit();
                }
            }
        });
*/



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

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE, recipe);
        startActivity(intent);
    }
}
