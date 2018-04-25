package com.leadinsource.bakingapp.db;

import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Content Provider for the widget
 */

public class Provider extends ContentProvider {

    public static final int RECIPES = 100;
    public static final int SINGLE_RECIPE = 101;
    public static final int INGREDIENTS = 201;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_RECIPES + "/#", SINGLE_RECIPE);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_INGREDIENTS + "/#", INGREDIENTS);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = uriMatcher.match(uri);
        final Cursor result;
        RecipeDao dao = Room.databaseBuilder(getContext(), AppDatabase.class, "data").allowMainThreadQueries().build().recipeDao();
        switch (code) {
            case RECIPES:
                result = dao.getAlRecipesInCursor();
                break;
            case SINGLE_RECIPE:
                result = dao.getRecipeByRecipeId(ContentUris.parseId(uri));
                break;
            case INGREDIENTS:
                result = dao.getIngredientByRecipeId(ContentUris.parseId(uri));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("Unknown uri " + uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

}
