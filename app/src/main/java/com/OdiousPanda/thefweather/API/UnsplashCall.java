package com.OdiousPanda.thefweather.API;

import com.OdiousPanda.thefweather.DataModel.Unsplash.Unsplash;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UnsplashCall {
    @GET("photos/random/?client_id=" + Constant.UNSPLASH_KEY + "&orientation=" + Constant.UNSPLASH_ORIENTATION_POTRAIT)
    Call<Unsplash> getRandomPotrait();

    @GET("photos/random/?client_id=" + Constant.UNSPLASH_KEY + "&orientation=" + Constant.UNSPLASH_ORIENTATION_LANDSCAPE)
    Call<Unsplash> getRandomLandscape();
}
