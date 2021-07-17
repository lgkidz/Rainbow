package com.odiousPanda.rainbowKt.apis

import com.odiousPanda.rainbowKt.model.dataSource.Nearby.NearbySearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbySearchCall {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun searchNearby(
        @Query("location") locationString: String?,
        @Query("radius") radius: Int,
        @Query("keyword") keyword: String?,
        @Query("key") apiKey: String?
    ): Response<NearbySearch>
}