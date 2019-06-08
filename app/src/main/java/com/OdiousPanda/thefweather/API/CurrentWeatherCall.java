package com.OdiousPanda.thefweather.API;

import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrentWeatherCall {
    @GET(Constant.CURRENT_WEATHER_LINK + "?" + Constant.API_KEY_PARAM + Constant.API_KEY)
    Call<CurrentWeather> getCurrentWeatherDataByCityName(@Query(Constant.CITY_NAME_PARAM) String cityName);

    @GET(Constant.CURRENT_WEATHER_LINK + "?" + Constant.API_KEY_PARAM + Constant.API_KEY)
    Call<CurrentWeather> getCurrentWeatherDataByCoordinate(@Query(Constant.LATITUDE_PARAM) String lat, @Query(Constant.LONGITUDE_PARAM) String lon);
}
