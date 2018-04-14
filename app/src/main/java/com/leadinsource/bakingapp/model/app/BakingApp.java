package com.leadinsource.bakingapp.model.app;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.leadinsource.bakingapp.BuildConfig;

import timber.log.Timber;

/**
 * App
 */

public class BakingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if(priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            // can be changed to some crash reporting tool later
            Log.e(tag, message);
        }
    }
}
