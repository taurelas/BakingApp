package com.leadinsource.bakingapp.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.leadinsource.bakingapp.db.DataContract;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Matt on 20/04/2018.
 */

public class RecipeWithData {
    @Embedded
    public Recipe recipe;

    @Relation(parentColumn = DataContract.Recipe.UID, entityColumn = DataContract.Ingredient.RECIPE_ID, entity = Ingredient.class)
    public List<Ingredient> ingredients;

    @Relation(parentColumn = DataContract.Recipe.UID, entityColumn = DataContract.Step.RECIPE_ID, entity = Step.class)
    public List<Step> steps;

    public RecipeWithData(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void passDataDown() {
        recipe.setIngredients(ingredients.toArray(new Ingredient[ingredients.size()]));
        recipe.setSteps(steps.toArray(new Step[steps.size()]));
    }

    public void updateData() {
        ingredients = Arrays.asList(recipe.getIngredients());
        steps = Arrays.asList(recipe.getSteps());
    }


}
