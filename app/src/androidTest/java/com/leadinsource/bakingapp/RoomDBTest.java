package com.leadinsource.bakingapp;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.leadinsource.bakingapp.db.AppDatabase;
import com.leadinsource.bakingapp.db.RecipeDao;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.RecipeWithData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Testing Room DB
 */
@RunWith(AndroidJUnit4.class)
public class RoomDBTest {
    private RecipeDao recipeDao;
    private AppDatabase db;

    @Before
    public void initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase.class).build();
        recipeDao = db.recipeDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertRecipeAndReadIt() throws Exception {
        Recipe recipe = TestUtil.createRecipe("Nutella piechart");

        TestUtil.insertRecipe(recipeDao, recipe);

        List<RecipeWithData> recipes = recipeDao.getAll();

        RecipeWithData recipeWithData = recipes.get(0);

        recipeWithData.passDataDown();

        assertEquals(recipe.getName(), recipeWithData.getRecipe().getName());
        assertEquals(recipe.getSteps()[0].getDescription(), recipeWithData.getSteps().get(0).getDescription());
        assertEquals(recipe.getSteps().length, recipeWithData.getSteps().size());
        assertEquals(recipe.getIngredients()[0].getIngredient(), recipeWithData.getIngredients().get(0).getIngredient());
        assertEquals(recipe.getIngredients().length, recipeWithData.getIngredients().size());

    }
}
