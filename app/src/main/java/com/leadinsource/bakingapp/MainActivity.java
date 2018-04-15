package com.leadinsource.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Steps;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainListAdapter.Callback {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d("App start");
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

    public static final int data = 5;

    public static final String EXTRA_DATA = "com.example.app.DATA";


    @Override
    public void onClick(Recipe recipe) {

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(EXTRA_DATA, recipe);

        startActivity(intent);

        for(Steps step : recipe.getSteps()) {
            Timber.d("Step %s %s", step.getId(), step.getShortDescription());
        }

        /*Toast.makeText(MainActivity.this, "Clicked by interface " + recipe.getName(),
                Toast.LENGTH_SHORT).show();*/
    }
}
