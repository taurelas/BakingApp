package com.leadinsource.bakingapp.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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

    @Insert
    long[] insertAll(Recipe... recipes);

    @Insert
    void insertAll(Step... steps);

    @Insert
    void insertAll(Ingredient... ingredients);
}
