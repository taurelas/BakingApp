package com.leadinsource.bakingapp.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Ingredient;

import java.util.List;

import timber.log.Timber;

/**
 * Fragment displaying a list of ingredients
 */

public class IngredientsFragment extends Fragment {

    static final String EXTRA_INGREDIENTS = "extra_ingredients";
    private RecipeActivityViewModel viewModel;

    Ingredient[] ingredients;
    private TextView textView;

    public IngredientsFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(RecipeActivityViewModel.class);

        if (savedInstanceState==null && getArguments().containsKey(EXTRA_INGREDIENTS)) {
            Parcelable[] parcelables = getArguments().getParcelableArray(EXTRA_INGREDIENTS);
            ingredients = new Ingredient[parcelables.length];
            for(int i=0; i<parcelables.length;i++) {
                ingredients[i] = (Ingredient) parcelables[i];
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            Parcelable[] parcelables = getArguments().getParcelableArray(EXTRA_INGREDIENTS);
            ingredients = new Ingredient[parcelables.length];
            for(int i=0; i<parcelables.length;i++) {
                ingredients[i] = (Ingredient) parcelables[i];
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        textView = rootView.findViewById(R.id.tvIngredients);


        viewModel.displayIngredients().observe(getActivity(), new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                StringBuilder sb = new StringBuilder();
                if (ingredients != null) {
                    for(Ingredient ingredient : ingredients) {
                        sb.append(ingredient.getIngredient()).append(" ");
                        sb.append(ingredient.getQuantity()).append(" ");
                        sb.append(ingredient.getMeasure()).append("\n");
                    }

                    textView.setText(sb.toString());
                } else {
                    Timber.d("Ingredients are null");
                    textView.setText(R.string.no_ingredients);
                }
            }
        });




        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray( EXTRA_INGREDIENTS, ingredients);
    }
}
