package com.odiousPanda.rainbowKt.apis

import com.odiousPanda.rainbowKt.model.dataSource.unsplash.Unsplash
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashCall {
    @GET("photos/random/")
    suspend fun getRandom(
        @Query("client_id") key: String = APIConstants.UNSPLASH_KEY,
        @Query("orientation") orientation: String = APIConstants.UNSPLASH_ORIENTATION_PORTRAIT
    ): Response<Unsplash>

    @GET("photos/random/")
    suspend fun getRandomWeather(
        @Query("client_id") key: String = APIConstants.UNSPLASH_KEY,
        @Query("orientation") orientation: String = APIConstants.UNSPLASH_ORIENTATION_PORTRAIT,
        @Query("query") query: String?
    ): Response<Unsplash>

}