package com.leadinsource.bakingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Steps;

/**
 * Adapter for Steps in RecipeActivity
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

    interface Callback {
        void onClick(Steps step);
    }

    private Recipe data;
    private boolean twoPane;
    private Callback callback;

    RecipeAdapter(Callback callback, Recipe recipe, boolean twoPane) {
        data = recipe;
        this.twoPane = twoPane;
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
        return data.getSteps().length;
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
