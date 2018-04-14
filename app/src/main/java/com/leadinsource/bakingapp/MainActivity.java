package com.leadinsource.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.leadinsource.bakingapp.model.Recipe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainListAdapter.Callback {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, getResources().getInteger(R.integer.main_list_columns));
        recyclerView.setLayoutManager(lm);

        viewModel.getRecipeNames().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeNames) {
                recyclerView.setAdapter(new MainListAdapter(MainActivity.this, recipeNames));
            }
        });
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(MainActivity.this, "Clicked by interface " + position,
                Toast.LENGTH_SHORT).show();
    }
}
