package com.leadinsource.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

import timber.log.Timber;

/**
 * Created by Matt on 17/04/2018.
 */

public class StepListFragment extends Fragment implements RecipeAdapter.Callback {

    boolean mTwoPane;
    private MainActivityViewModel viewModel;
    private View rootView;

    public StepListFragment() {
        // empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_step_list, container, false);

        if (rootView.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Timber.d("Two pane is true");
            mTwoPane = true;
        } else {
            Timber.d("Two pane is false");
        }

        if(getArguments()!=null) {
            RecyclerView recyclerView = rootView.findViewById(R.id.rv_steps_list);
            assert recyclerView != null;
            recyclerView.setAdapter(new RecipeAdapter(StepListFragment.this, (Recipe) getArguments().get(MainActivity.EXTRA_RECIPE)));
        }

        return rootView;
    }

    @Override
    public void onClick(Step step) {
        viewModel.setCurrentStep(step);
        Toast.makeText(getContext(), "Clicked step", Toast.LENGTH_SHORT).show();
    }
}
