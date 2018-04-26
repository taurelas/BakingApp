package com.leadinsource.bakingapp.ui.recipe;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

import timber.log.Timber;

/**
 * Adapter for Step in RecipeActivity
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

    interface Callback {
        void onClick(Step step);
    }

    private Recipe data;
    private Callback callback;

    RecipeAdapter(Callback callback, Recipe recipe) {
        Timber.d("Recipe is "+null);
        data = recipe;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.idView.setText(data.getSteps()[position].getId());
        holder.contentView.setText(data.getSteps()[position].getShortDescription());
    }

    @Override
    public int getItemCount() {
        if(data==null || data.getSteps()==null) {
            Timber.d("Something is null");
            return 0;
        } else {
            return data.getSteps().length;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView idView;
        final TextView contentView;
        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(data.getSteps()[getAdapterPosition()]);
                }
            });
            idView = itemView.findViewById(R.id.id_text);
            contentView = itemView.findViewById(R.id.content);
        }
    }
}
