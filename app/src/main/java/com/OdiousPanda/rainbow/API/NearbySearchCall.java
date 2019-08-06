package com.OdiousPanda.rainbow.API;

import com.OdiousPanda.rainbow.DataModel.Nearby.NearbySearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbySearchCall {
    @GET("maps/api/place/nearbysearch/json")
    Call<NearbySearch> searchNearby(@Query("location") String locationString,
                                    @Query("radius") int radius,
                                    @Query("keyword") String keyword,
                                    @Query("key") String apiKey);
}
