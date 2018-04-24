package com.leadinsource.bakingapp.db;

/**
 * Created by Matt on 20/04/2018.
 */

public class DataContract {
    public class Recipe {
        public static final String TABLE_NAME = "recipes";
        public static final String UID = "uid";
        public static final String ID = "id";

    }

    public static class Ingredient {
        public static final String TABLE_NAME = "ingredients";
        public static final String RECIPE_ID = "recipe_id";
    }

    public static class Step {
        public static final String TABLE_NAME = "steps";
        public static final String RECIPE_ID = "recipe_id";
    }

}
