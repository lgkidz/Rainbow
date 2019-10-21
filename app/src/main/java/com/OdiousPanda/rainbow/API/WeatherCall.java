package com.OdiousPanda.rainbow.API;

import com.OdiousPanda.rainbow.DataModel.Weather.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherCall {
    @GET(APIConstants.API_KEY + "/{lat},{lon}")
    Call<Weather> getWeather(@Path("lat") String lat, @Path("lon") String lon, @Query("lang") String lang);
}
