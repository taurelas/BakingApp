package com.leadinsource.bakingapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.leadinsource.bakingapp.repo.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 14/04/2018.
 */

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<List<String>> recipes;
    private Repository repo;

    public MainActivityViewModel() {
        repo = Repository.getInstance();
    }



    public LiveData<List<String>> getRecipeNames() {
        if(recipes==null) {
            recipes = new MutableLiveData<>();
            ArrayList<String> list = new ArrayList<>();
            list.add("Test 1");
            list.add("Test2");
            recipes.setValue(list);
        }

        return recipes;
    }
}
