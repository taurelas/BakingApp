package com.leadinsource.bakingapp.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.db.DataContract;
import com.leadinsource.bakingapp.db.DbUtil;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.ui.main.MainListAdapter;
import com.leadinsource.bakingapp.ui.recipe.RecipeActivity;

import java.util.List;

import timber.log.Timber;

/**
 * The configuration screen for the {@link IngredientsWidget IngredientsWidget} AppWidget.
 */
public class IngredientsWidgetConfigureActivity extends Activity implements MainListAdapter.Callback {

    private static final String PREFS_NAME = "com.leadinsource.bakingapp.widget.IngredientsWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Timber.d("Starting Configure Activity");
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredients_widget_configure);

        // setting up recyclerView with recipes
        Cursor cursor = getContentResolver().query(DataContract.Recipe.CONTENT_URI, null,null,null,null);
        List<Recipe> data = DbUtil.getRecipesFromCursor(cursor);
        RecyclerView recyclerView = findViewById(R.id.rv_candidates);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, getResources().getInteger(R.integer.main_list_columns));
        recyclerView.setLayoutManager(lm);
        MainListAdapter adapter = new MainListAdapter(this, data);
        recyclerView.setAdapter(adapter);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void onClick(Recipe recipe) {

        saveRecipeId(this, widgetId, recipe.uid);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);


        appWidgetManager.updateAppWidget(widgetId, buildRemoteViews(this, widgetId));
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.list_view);


        Toast.makeText(this, "Clicked recipe " + recipe.uid+ " "+ recipe.getName(), Toast.LENGTH_SHORT).show();

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        Timber.d("Finishing configuring activity");
        finish();
    }

    public static RemoteViews buildRemoteViews(final Context context, final int appWidgetId) {
        Intent listViewIntent = new Intent(context, WidgetService.class);
        listViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        listViewIntent.setData(Uri.parse(listViewIntent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        widget.setRemoteAdapter(R.id.list_view, listViewIntent);

        Intent clickIntent = new Intent(context, RecipeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.list_view, pendingIntent);

        widget.setEmptyView(R.id.list_view, R.id.empty_view);

        return widget;
    }

    public IngredientsWidgetConfigureActivity() {
        super();
    }

     // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipeId(Context context, int appWidgetId, int recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, recipeId);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
    }

    static void deleteRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

}

