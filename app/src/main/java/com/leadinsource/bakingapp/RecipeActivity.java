package com.leadinsource.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.leadinsource.bakingapp.dummy.DummyContent;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Steps;

import java.util.List;

import timber.log.Timber;

/**
 * An activity representing a list of steps . This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
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
        setContentView(R.layout.activity_item_list);
        Timber.d("Starting RecipeActivity");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

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

        RecyclerView recyclerView = findViewById(R.id.rv_steps_list);
        assert recyclerView != null;
        recyclerView.setAdapter(new RecipeAdapter(this, recipe, mTwoPane));
    }

    @Override
    public void onClick(Steps step) {
        Toast.makeText(this, "Hit step "+ step.getDescription(), Toast.LENGTH_SHORT).show();

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, step.getId());
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, step.getId());

            startActivity(intent);
        }

    }
}
