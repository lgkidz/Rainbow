package com.OdiousPanda.rainbow.API;

import com.OdiousPanda.rainbow.DataModel.Unsplash.Unsplash;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashCall {
    @GET("photos/random/")
    Call<Unsplash> getRandom(@Query("client_id") String key, @Query("orientation") String orientation);

    @GET("photos/random/")
    Call<Unsplash> getRandomWeather(@Query("client_id") String key, @Query("orientation") String orientation, @Query("query") String query);

}
