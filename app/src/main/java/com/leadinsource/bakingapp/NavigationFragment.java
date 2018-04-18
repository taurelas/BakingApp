package com.leadinsource.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import timber.log.Timber;

/**
 * Created by Matt on 17/04/2018.
 */

public class NavigationFragment extends Fragment {

    MainActivityViewModel viewModel;
    private Button previousButton;
    private Button nextButton;


    public NavigationFragment() {
    // required empty fragment
}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Creating view");
        View rootView = inflater.inflate(R.layout.navigation_buttons, container, false);

        previousButton = rootView.findViewById(R.id.btn_previous);
        nextButton = rootView.findViewById(R.id.btn_next);


        if(nextButton!=null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.moveToNext();
                }
            });
        }

        if(previousButton!=null) {
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.moveToPrevious();
                }
            });
        }


        if(viewModel.isFirst()) {

            previousButton.setVisibility(View.INVISIBLE);
        }


        viewModel.isLastStep().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLast) {
                if (isLast!=null) {
                    if(isLast) {
                        nextButton.setVisibility(View.INVISIBLE);
                    } else {
                        nextButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return rootView;
    }
}
