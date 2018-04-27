package com.leadinsource.bakingapp.ui.recipe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Ingredient;

import timber.log.Timber;

/**
 * Fragment displaying a list of ingredients
 */

public class IngredientsFragment extends Fragment {

    static final String EXTRA_INGREDIENTS = "extra_ingredients";

    Ingredient[] ingredients;

    public IngredientsFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(EXTRA_INGREDIENTS)) {
            ingredients = (Ingredient[]) getArguments().getParcelableArray(EXTRA_INGREDIENTS);

        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        TextView textView = rootView.findViewById(R.id.tvIngredients);
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

        return rootView;
    }
}
