package com.leadinsource.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Service for ListView within the widget
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}
