package com.leadinsource.bakingapp.db;

import android.net.Uri;

/**
 * Data Contract for DB operations
 */

public class DataContract {

    public static final String AUTHORITY = "com.leadinsource.bakingapp.db";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_RECIPES = "RECIPES";
    public static final String PATH_INGREDIENTS = "INGREDIENTS";

    public static class Recipe {
        public static final String TABLE_NAME = "recipes";
        public static final String UID = "uid";
        public static final String ID = "id";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        public static final String NAME = "name";
    }

    public static class Ingredient {
        public static final String ID = "id";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
        public static final String TABLE_NAME = "ingredients";
        public static final String RECIPE_ID = "recipe_id";
        public static final String MEASURE = "measure";
        public static final String INGREDIENT = "ingredient";
        public static final String QUANTITY = "quantity";
    }

    public static class Step {
        public static final String TABLE_NAME = "steps";
        public static final String RECIPE_ID = "recipe_id";
    }

}
