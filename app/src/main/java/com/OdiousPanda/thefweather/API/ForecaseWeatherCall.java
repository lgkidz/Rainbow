package com.OdiousPanda.thefweather.API;

import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecaseWeatherCall {
    @GET(Constant.FORECAST_LINK + "?" + Constant.API_KEY_PARAM + Constant.API_KEY)
    Call<ForecastWeather> getForecastWeatherByCityName(@Query(Constant.CITY_NAME_PARAM) String cityName);

    @GET(Constant.FORECAST_LINK + "?" + Constant.API_KEY_PARAM + Constant.API_KEY)
    Call<ForecastWeather> getForecastWeatherByCityCoordinate(@Query(Constant.LATITUDE_PARAM) String lat, @Query(Constant.LONGITUDE_PARAM) String lon);
}
