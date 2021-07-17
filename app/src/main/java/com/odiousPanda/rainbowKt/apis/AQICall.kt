package com.odiousPanda.rainbowKt.apis

import com.odiousPanda.rainbowKt.model.dataSource.aqi.Aqi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AQICall {
    @GET("feed/geo:{lat};{lon}/?token=" + APIConstants.AQI_API_KEY)
    suspend fun getAirQuality(
        @Path("lat") lat: String,
        @Path("lon") lon: String
    ): Response<Aqi>
}