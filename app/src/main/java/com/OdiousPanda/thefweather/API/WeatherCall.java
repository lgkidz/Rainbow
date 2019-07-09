package com.OdiousPanda.thefweather.API;

import com.OdiousPanda.thefweather.DataModel.Weather.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherCall {
    @GET(Constant.API_KEY + "/{lat},{lon}")
    Call<Weather> getWeather(@Path("lat") String lat, @Path("lon") String lon);
}
