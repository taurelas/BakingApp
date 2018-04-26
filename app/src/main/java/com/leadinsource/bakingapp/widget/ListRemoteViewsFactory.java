package com.leadinsource.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.db.DataContract;

import timber.log.Timber;

/**
 * RemoteViewsFactory for filling ListView within the widget with data
 */
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String EXTRA_RECIPE_ID = "com.leadinsource.bakingapp.extra_recipe_id";
    private Context context;
    private Cursor cursor;
    private int recipeId;
    private Intent recipeIntent = new Intent();


    public ListRemoteViewsFactory(Context applicationContext, Intent intent) {
        context = applicationContext;
        Timber.d("starting views factory");
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        recipeId = IngredientsWidgetConfigureActivity.loadRecipeIdPref(context, widgetId);
        Bundle extras = new Bundle();
        extras.putInt(EXTRA_RECIPE_ID, recipeId);
        recipeIntent.putExtras(extras);
        Timber.d("Adding %s to intent %s", recipeId, intent.getIntExtra(ListRemoteViewsFactory.EXTRA_RECIPE_ID,-1));
    }


    @Override
    public void onCreate() {

    }

    //
    // called ON START (when the RemoteViewFactory is created and when notifyAppWidgetViewDataChanged is called
    // this would correspond to notifyDataSetChanged() in a LV or RV adapters
    @Override
    public void onDataSetChanged() {
        Uri uri = DataContract.Ingredient.CONTENT_URI.buildUpon().appendPath("" + recipeId).build();
        if (cursor != null) {
            cursor.close();
        }
        cursor = context.getContentResolver().query(uri, null, null, null, null);
    }

    @Override
    public void onDestroy() {
        // we could for example close the cursor or db here
        cursor.close();
    }

    @Override
    public int getCount() {
        // for instance cursor.getCount
        if (cursor == null) {
            return 0;
        }

        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // here goes the binding logic

        cursor.moveToPosition(position);

        String ingredient = cursor.getString(cursor.getColumnIndex(DataContract.Ingredient.INGREDIENT));
        String measure = cursor.getString(cursor.getColumnIndex(DataContract.Ingredient.MEASURE));
        String quantity = cursor.getString(cursor.getColumnIndex(DataContract.Ingredient.QUANTITY));

        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.listview_row);

        row.setTextViewText(R.id.ingredient_text, ingredient + " "+ quantity + " "+ measure);

        row.setOnClickFillInIntent(R.id.ingredient_text, recipeIntent);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
