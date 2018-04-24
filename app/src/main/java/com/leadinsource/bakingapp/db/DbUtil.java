package com.leadinsource.bakingapp.db;

import android.os.AsyncTask;

import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

import java.util.List;

/**
 * Created by Matt on 24/04/2018.
 */

public class DbUtil {
    public static void insertRecipe(RecipeDao recipeDao, Recipe recipe) {
        long ids[] = recipeDao.insertAll(recipe);

        long id = ids[0];

        Step[] steps = recipe.getSteps();

        for(Step step : steps) {
            step.recipeId = (int) id;
        }

        Ingredient[] ingredients = recipe.getIngredients();

        for(Ingredient ingredient : ingredients) {
            ingredient.recipeId = (int) id;
        }

        recipeDao.insertAll(steps);
        recipeDao.insertAll(ingredients);
    }

    public static void insertRecipes(RecipeDao recipeDao, List<Recipe> recipes) {

        new MyAsync().execute(new Param(recipes, recipeDao));

        /*for(Recipe recipe : recipes) {
            insertRecipe(recipeDao, recipe);
        }*/
    }

    private static class MyAsync extends AsyncTask<Param, Void, Void> {


        @Override
        protected Void doInBackground(Param... params) {
            for(Recipe recipe : params[0].recipes) {
                insertRecipe(params[0].recipeDao, recipe);
            }

            return null;
        }
    }

    private static class Param {
        public List<Recipe> recipes;
        public RecipeDao recipeDao;

        public Param(List<Recipe> recipes, RecipeDao recipeDao) {
            this.recipes = recipes;
            this.recipeDao = recipeDao;
        }

    }
}
