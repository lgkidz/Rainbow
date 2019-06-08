package com.OdiousPanda.thefweather.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static CurrentWeatherCall createCurrentWeatherCall(){
        return retrofit.create(CurrentWeatherCall.class);
    }

    public static ForecaseWeatherCall createForecastWeatherCall(){
        return retrofit.create(ForecaseWeatherCall.class);
    }
}
