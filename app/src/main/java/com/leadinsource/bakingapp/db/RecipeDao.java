package com.leadinsource.bakingapp.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.leadinsource.bakingapp.model.Ingredient;
import com.leadinsource.bakingapp.model.Recipe;
import com.leadinsource.bakingapp.model.RecipeWithData;
import com.leadinsource.bakingapp.model.Step;

import java.util.List;

/**
 * DAO for Room db
 */
@Dao
public interface RecipeDao {
    @Query("SELECT * FROM "+ DataContract.Recipe.TABLE_NAME)
    List<RecipeWithData> getAll();

    @Query("SELECT * FROM "+ DataContract.Recipe.TABLE_NAME)
    LiveData<List<RecipeWithData>> getAllLiveData();

    @Query("SELECT * FROM "+ DataContract.Recipe.TABLE_NAME)
    Cursor getAlRecipesInCursor();

    @Query("SELECT * FROM "+ DataContract.Recipe.TABLE_NAME + " WHERE "+ DataContract.Recipe.ID + " = :id")
    Cursor getRecipeByRecipeId(long id);

    @Query("SELECT * FROM "+ DataContract.Recipe.TABLE_NAME + " WHERE "+ DataContract.Recipe.ID + " = :id")
    LiveData<List<Recipe>> getRecipeLiveDataByRecipeId(long id);

    @Query("SELECT * FROM "+ DataContract.Ingredient.TABLE_NAME + " WHERE "+ DataContract.Ingredient.RECIPE_ID + " = :id")
    Cursor getIngredientByRecipeId(long id);

    @Insert
    long[] insertAll(Recipe... recipes);

    @Insert
    void insertAll(Step... steps);

    @Insert
    void insertAll(Ingredient... ingredients);
}
