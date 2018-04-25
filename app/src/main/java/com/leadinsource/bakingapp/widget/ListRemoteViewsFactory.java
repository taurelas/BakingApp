package com.leadinsource.bakingapp.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * RemoteViewsFactory for filling ListView within the widget with data
 */
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    Context context;

    public ListRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
    }


    @Override
    public void onCreate() {

    }
    //
    // called ON START (when the RemoteViewFactory is created and when notifyAppWidgetViewDataChanged is called
    // this would correspond to notifyDataSetChanged() in a LV or RV adapters
    @Override
    public void onDataSetChanged() {
        // essentially we need to query the data source here
        // be it a db or content provider etc
    }

    @Override
    public void onDestroy() {
        // we could for example close the cursor or db here
    }

    @Override
    public int getCount() {
        // for instance cursor.getCount

        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // here goes the binding logic

        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
