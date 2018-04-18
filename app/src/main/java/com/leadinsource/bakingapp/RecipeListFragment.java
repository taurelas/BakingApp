package com.leadinsource.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leadinsource.bakingapp.model.Recipe;

import java.util.List;

/**
 * Created by Matt on 17/04/2018.
 */

public class RecipeListFragment extends Fragment implements MainListAdapter.Callback {

    MainActivityViewModel viewModel;
    private RecyclerView recyclerView;

    public RecipeListFragment() {
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.main_list_columns));
        recyclerView.setLayoutManager(lm);

        viewModel.getRecipeNames().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeNames) {
                recyclerView.setAdapter(new MainListAdapter(RecipeListFragment.this, recipeNames));
            }
        });

        return rootView;

    }

    @Override
    public void onClick(Recipe recipe) {

        viewModel.setRecipeToDisplay(recipe);

        /*



        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(EXTRA_DATA, recipe);

        startActivity(intent);

        for(Step step : recipe.getSteps()) {
            Timber.d("Step %s %s", step.getId(), step.getShortDescription());
        }
*/

        Toast.makeText(getContext(), "Clicked by interface " + recipe.getName(),
                Toast.LENGTH_SHORT).show();
    }
}
