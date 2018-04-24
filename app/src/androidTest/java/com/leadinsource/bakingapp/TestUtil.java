package com.leadinsource.bakingapp;

import com.leadinsource.bakingapp.db.RecipeDao;
import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.Step;

/**
 * Created by Matt on 20/04/2018.
 */

public class TestUtil {
    public static Recipe createRecipe(String name) {
        Recipe recipe = new Recipe();

        recipe.setName(name);

        Step[] steps = new Step[7];

        for (int i = 0; i < 7; i++) {
            steps[i] = new Step();
        }

        steps[0].setId("0");
        steps[0].setDescription("Recipe Introduction");
        steps[1].setId("1");
        steps[1].setDescription("Starting prep");
        steps[2].setId("2");
        steps[2].setDescription("Prep the cookie crust.");
        steps[3].setId("3");
        steps[3].setDescription("Press the crust into baking form.");
        steps[4].setId("4");
        steps[4].setDescription("Start filling prep");
        steps[5].setId("5");
        steps[5].setDescription("Finish filling prep");
        steps[6].setId("6");
        steps[6].setDescription("Finishing Steps");


        Ingredient[] ingredients = new Ingredient[5];
        for (int i = 0; i < 5; i++) {
            ingredients[i] = new Ingredient();
            ingredients[i].setIngredient("Name of " + i + " ingredient");
        }

        recipe.setIngredients(ingredients);

        recipe.setSteps(steps);

        return recipe;
    }

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
}
