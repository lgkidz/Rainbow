package com.odiousPanda.rainbowKt.apis

import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherCall {
    @GET(APIConstants.API_KEY + "/{lat},{lon}")
    suspend fun getWeather(
        @Path("lat") lat: String,
        @Path("lon") lon: String,
        @Query("lang") lang: String?
    ): Response<Weather>
}