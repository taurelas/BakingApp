package com.leadinsource.bakingapp.ui.main;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.db.DataContract;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.ui.idlingresource.SimpleIdlingResource;
import com.leadinsource.bakingapp.ui.recipe.RecipeActivity;
import com.leadinsource.bakingapp.widget.IngredientsWidget;

import java.util.List;

import timber.log.Timber;

/**
 * Displays list of recipes obtained from a remote json
 */
public class MainActivity extends AppCompatActivity implements MainListAdapter.Callback {

    public static final String EXTRA_RECIPE = "extra_recipe";
    public static final String EXTRA_STEP = "extra_step";
    private RecyclerView recyclerView;

    @Nullable private SimpleIdlingResource idlingResource;

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
                if (idle!=null) {
                    getIdlingResource();
                    idlingResource.setIsIdleNow(idle);
                }
            }
        });


        Cursor cursor = getContentResolver().query(DataContract.Recipe.CONTENT_URI, null, null,null,null);
        Timber.d("Cursor count: "+cursor.getCount());
        //test content provider

        /*new AsyncTask<Uri, Void, Cursor>() {

            @Override
            protected Cursor doInBackground(Uri... uris) {
                return getContentResolver().query(DataContract.Recipe.CONTENT_URI, null, null,null,null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                Timber.d("Cursor count: "+cursor.getCount());
            }
        }.execute(DataContract.Recipe.CONTENT_URI);*/


    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE, recipe);
        startActivity(intent);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if(idlingResource==null) {
            idlingResource = new SimpleIdlingResource();
        }

        return idlingResource;
    }
}
