package com.leadinsource.bakingapp.ui.recipe;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

/**
 * Adapter for Step in RecipeActivity
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ViewType {
        static final int STEP = 0;
        static final int INGREDIENTS = 1;
    }

    // interface for clicks
    interface Callback {
        void onClick(Step step);

        void onClick(Ingredient[] ingredients);
    }

    interface UpdateViewHolder {
        void bindView(Step step);
    }

    private Recipe data;
    private Callback callback;

    RecipeAdapter(Callback callback, Recipe recipe) {
        data = recipe;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        // first item will link to ingredients, the rest are steps
        if (position == 0) {
            return ViewType.INGREDIENTS;
        } else {
            return ViewType.STEP;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // we use layout dependant on viewType
        if(viewType==ViewType.INGREDIENTS) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_ingredients, parent, false);

            return new IngredientsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);

            return new RecipeStepViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position > 0)
            ((UpdateViewHolder) holder).bindView(data.getSteps()[position-1]);
    }

    @Override
    public int getItemCount() {
        if (data == null || data.getSteps() == null) {
            return 0;
        } else {
            return data.getSteps().length+1; // ingredients gives +1
        }

    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder implements UpdateViewHolder {
        final TextView idView;
        final TextView contentView;

        RecipeStepViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(data.getSteps()[getAdapterPosition()-1]);
                }
            });
            idView = itemView.findViewById(R.id.id_text);
            contentView = itemView.findViewById(R.id.content);
        }

        public void bindView(Step step) {
            idView.setText(step.getId());
            contentView.setText(step.getShortDescription());
        }
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder implements UpdateViewHolder {

        IngredientsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(data.getIngredients());
                }
            });

        }

        public void bindView(Step step) {
            // do nothing
        }
    }


}
