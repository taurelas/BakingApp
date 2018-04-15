package com.leadinsource.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

import timber.log.Timber;

/**
 * An activity representing a list of steps . This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.Callback {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Timber.d("Starting RecipeActivity");

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Timber.d("Two pane is true");
            mTwoPane = true;
        }

        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra(MainActivity.EXTRA_DATA);

        setTitle(recipe.getName());

        RecyclerView recyclerView = findViewById(R.id.rv_steps_list);
        assert recyclerView != null;
        recyclerView.setAdapter(new RecipeAdapter(this, recipe));
    }

    @Override
    public void onClick(Step step) {
        Toast.makeText(this, "Hit step "+ step.getDescription(), Toast.LENGTH_SHORT).show();

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(StepDetailFragment.EXTRA_STEP, step);
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(StepDetailFragment.EXTRA_STEP, step);

            startActivity(intent);
        }

    }
}
