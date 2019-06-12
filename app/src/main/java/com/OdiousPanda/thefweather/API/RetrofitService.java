package com.OdiousPanda.thefweather.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofitWeather = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static Retrofit retrofitAQI = new Retrofit.Builder()
            .baseUrl(Constant.AQI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static WeatherCall createWeatherCall(){
        return retrofitWeather.create(WeatherCall.class);
    }

    public static AQICall createAQICall(){return retrofitAQI.create(AQICall.class);}
}
