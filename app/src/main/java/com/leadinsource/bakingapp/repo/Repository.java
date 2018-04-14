package com.leadinsource.bakingapp.repo;

/**
 * Created by Matt on 14/04/2018.
 */

public class Repository {
    private static Repository instance;

    public static Repository getInstance() {
        if(instance==null) {
            instance = new Repository();
        }

        return instance;
    }

    //public LiveData<> get
}
