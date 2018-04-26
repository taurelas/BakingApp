package com.leadinsource.bakingapp.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Recipe;

import java.util.List;

/**
 * Adapter for GridView on the Main Screen
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder>{

    private Callback callback;
    private List<Recipe> data;

    public interface Callback {
        void onClick(Recipe recipe);
    }

    public MainListAdapter(Callback callback, List<Recipe> data) {
        this.callback = callback;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRecipeName.setText(data.get(position).getName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRecipeName;

        ViewHolder(View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(data.get(getAdapterPosition()));
                }
            });
        }
    }

}
