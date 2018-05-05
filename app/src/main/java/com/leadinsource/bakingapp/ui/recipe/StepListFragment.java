package com.leadinsource.bakingapp.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

import java.util.Arrays;

import timber.log.Timber;

/**
 * Fragment for List of steps of a given recipe
 */

public class StepListFragment extends Fragment implements RecipeAdapter.Callback {

    boolean mTwoPane;

    private RecipeActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private Parcelable state;

    public StepListFragment() {
        // empty constructor
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(RecipeActivityViewModel.class);
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_list, container, false);

        if (rootView.findViewById(R.id.step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Timber.d("Two pane is true");
            mTwoPane = true;
        } else {
            Timber.d("Two pane is false");
        }

        recyclerView = rootView.findViewById(R.id.rv_steps_list);


        assert recyclerView != null;

        viewModel.getCurrentRecipe().observe(getActivity(), new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe!=null) {
                    llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(new RecipeAdapter(StepListFragment.this, recipe));
                    if(state!=null) {
                        recyclerView.getLayoutManager().onRestoreInstanceState(state);
                    }

                }
            }
        });

        return rootView;
    }

    @Override
    public void onClick(Step step) {
        viewModel.setCurrentStep(step);
    }

    @Override
    public void onClick(Ingredient[] ingredients) {
        Toast.makeText(getContext(), "Clicked ingredients", Toast.LENGTH_SHORT).show();
        viewModel.displayIngredients(Arrays.asList(ingredients));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("RV", recyclerView.getLayoutManager().onSaveInstanceState());
        Timber.d("Saving instance state in StepListFragment");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            state = savedInstanceState.getParcelable("RV");
            Timber.d("Restoring instance state in StepListFragment");
        }
    }

}
