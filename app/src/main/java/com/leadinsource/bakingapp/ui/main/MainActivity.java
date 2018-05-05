package com.leadinsource.bakingapp.ui.main;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.ui.idlingresource.SimpleIdlingResource;
import com.leadinsource.bakingapp.ui.recipe.RecipeActivity;
import com.leadinsource.bakingapp.widget.IngredientsWidget;

import java.util.List;

import timber.log.Timber;

import static com.leadinsource.bakingapp.widget.ListRemoteViewsFactory.EXTRA_RECIPE_ID;

/**
 * Displays list of recipes obtained from a remote json
 */
public class MainActivity extends AppCompatActivity implements MainListAdapter.Callback {

    public static final String EXTRA_RECIPE = "extra_recipe";
    public static final String EXTRA_STEP = "extra_step";
    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;

    @Nullable
    private SimpleIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.d("App start");
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // ItemDecoration code as per https://stackoverflow.com/a/29168276/3886459

        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getInteger(R.integer.grid_spacing), getResources().getInteger(R.integer.main_list_columns)
        ));
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, getResources().getInteger(R.integer.main_list_columns));
        recyclerView.setLayoutManager(lm);
        if(recyclerViewState !=null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            recyclerViewState = null;
        }

        viewModel.getRecipeNames().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeNames) {
                recyclerView.setAdapter(new MainListAdapter(MainActivity.this, recipeNames));
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(MainActivity.this, IngredientsWidget.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
            }
        });

        viewModel.getIdleness().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean idle) {
                if (idle != null) {
                    getIdlingResource();
                    idlingResource.setIsIdleNow(idle);
                }
            }
        });
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        int id;
        if(recipe.uid>0) {
            id = recipe.uid;
        } else {
            id = Integer.valueOf(recipe.getId());
        }
        intent.putExtra(EXTRA_RECIPE_ID, id);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("RV", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null) {
            recyclerViewState = savedInstanceState.getParcelable("RV");
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }

        return idlingResource;
    }
}
