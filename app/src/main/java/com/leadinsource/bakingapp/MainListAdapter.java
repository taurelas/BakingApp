package com.leadinsource.bakingapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Adapter for GridView on the Main Screen
 */

public class MainListAdapter extends BaseAdapter{

    private Callback callback;

    interface Callback {
        void onClick(int position);
    }

    MainListAdapter(Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
