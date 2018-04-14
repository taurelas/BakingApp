package com.leadinsource.bakingapp.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Matt on 14/04/2018.
 */

public interface DownloadService {
    @GET
    Call<RecipesResponse> downloadRecipes(@Url String url);
}
